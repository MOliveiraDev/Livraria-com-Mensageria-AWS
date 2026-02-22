package com.microsservice.rental_service.service;

import com.microsservice.rental_service.domain.BookEntity;
import com.microsservice.rental_service.domain.RentalEntity;
import com.microsservice.rental_service.dto.*;
import com.microsservice.rental_service.exception.rental.BookNotFoundException;
import com.microsservice.rental_service.exception.rental.RentalCreationException;
import com.microsservice.rental_service.exception.rental.RentalNotFoundException;
import com.microsservice.rental_service.repository.BookRepository;
import com.microsservice.rental_service.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalService {

    private final BookRepository bookRepository;
    private final RentalRepository rentalRepository;
    private final SnsService snsService;

    @Value("${aws.sns.livro-alugado-topic-arn}")
    private String livroAludagoTopicArn;

    @Value("${aws.sns.livro-retornado-topic-arn}")
    private String bookReturnedTopicArn;

    @Transactional(rollbackFor = Exception.class)
    @Async
    public CompletableFuture<RentalResponseDTO> createRental(RentalRequestDTO rentalRequestDTO) {
        try {
            for (Long bookId : rentalRequestDTO.bookIds()) {
                if (!bookRepository.existsById(bookId)) {
                    throw new BookNotFoundException("Book not found with ID: " + bookId);
                }

                RentalEntity rental = new RentalEntity();
                rental.setBookId(bookId);
                rental.setEmail(rentalRequestDTO.email());
                rental.setRentalDate(LocalDate.now());
                rental.setReturnDate(rentalRequestDTO.returnDate());
                rentalRepository.save(rental);

                String bookTitle = String.valueOf(bookRepository.findById(bookId)
                        .map(BookEntity::getTitle));

                RentalCreatedEventDTO event = new RentalCreatedEventDTO(
                        rental.getBookId(),
                        bookTitle,
                        rental.getEmail(),
                        rental.getReturnDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                );
                log.info("Enviando evento SNS: {}", event);
                snsService.sendEvent(livroAludagoTopicArn, event);
            }
        } catch (Exception e) {
            log.error("Error creating rental: {}", e.getMessage());
            throw new RentalCreationException("Failed to create rental: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(new RentalResponseDTO(
                rentalRequestDTO.bookIds().stream().map(id -> bookRepository.findByBookId(id).get().getTitle()).toList(),
                rentalRequestDTO.email(),
                rentalRequestDTO.returnDate(),
                LocalDate.now()
        ));
    }

    public List<RentalResponseDTO> getRentalsByEmail(String email) {
        List<RentalEntity> rentals = rentalRepository.findByEmail(email);
        return rentals.stream()
                .collect(java.util.stream.Collectors.groupingBy(RentalEntity::getEmail))
                .entrySet().stream()
                .map(entry -> new RentalResponseDTO(
                        entry.getValue().stream()
                                .map(rental -> bookRepository.findById(rental.getBookId())
                                        .map(BookEntity::getTitle)
                                        .orElse("Unknown Book"))
                                .toList(),
                        entry.getKey(),
                        entry.getValue().getFirst().getReturnDate(),
                        entry.getValue().getFirst().getRentalDate()
                ))
                .toList();
    }

    @Transactional
    public BookReturnedResponseDTO sendBookReturnedEvent(BookReturnedRequestDTO request) {
            try {
                for (Long bookId : request.bookIds()) {

                    if (!bookRepository.existsById(bookId)) {
                        throw new BookNotFoundException("Book not found with ID: " + bookId);
                    }

                    if (!rentalRepository.existsByBookId(bookId))  {
                        throw new RentalNotFoundException("Book not rented with ID: " + bookId);
                    }

                    String bookTitle = (bookRepository.findByBookId(bookId)
                            .map(BookEntity::getTitle)).orElse("Unknown Book");

                    BookReturnedCreatedEventDTO event = new BookReturnedCreatedEventDTO(
                            request.bookIds(),
                            request.email()
                    );
                    snsService.sendEvent(bookReturnedTopicArn, event);
                    log.info("Evento de devolução enviado para bookId: {} , email: {} e nome do livro {}", bookId, request.email(), bookTitle);

                    List<RentalEntity> rentals = rentalRepository.findByBookId(bookId);
                    if (!rentals.isEmpty()) {
                        rentalRepository.deleteAll(rentals);
                    }
                }

            } catch (Exception e) {
                log.error("Erro ao enviar a devolução: {}", e.getMessage());
            }
        return new BookReturnedResponseDTO("Livros devolvidos com sucesso!");
    }
}
