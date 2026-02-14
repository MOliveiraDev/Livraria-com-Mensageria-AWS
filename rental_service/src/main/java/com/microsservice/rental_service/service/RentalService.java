package com.microsservice.rental_service.service;

import com.microsservice.rental_service.domain.BookEntity;
import com.microsservice.rental_service.domain.RentalEntity;
import com.microsservice.rental_service.dto.RentalCreatedEventDTO;
import com.microsservice.rental_service.dto.RentalRequestDTO;
import com.microsservice.rental_service.dto.RentalResponseDTO;
import com.microsservice.rental_service.exception.rental.BookNotFoundException;
import com.microsservice.rental_service.exception.rental.RentalCreationException;
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

                String bookTitle = bookRepository.findById(bookId)
                        .map(BookEntity::getTitle)
                        .orElse("Unknown Book");

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

    public void sendBookReturnedEvent(List<Long> bookIds) {
        for (Long bookId : bookIds) {
            try {
                String event = String.format("{\"bookId\":%d}", bookId);
                snsService.sendEvent(bookReturnedTopicArn, event);
                log.info("Evento de devolução enviado para bookId: {}", bookId);
            } catch (Exception e) {
                log.error("Erro ao enviar evento de devolução: {}", e.getMessage());
            }
        }
    }
}
