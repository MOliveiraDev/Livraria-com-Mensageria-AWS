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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalService {

    private final BookRepository bookRepository;
    private final RentalRepository rentalRepository;
    private final SnsService snsService;

    @Value("${aws.sns.livro-alugado-topic-arn}")
    private String livroAludagoTopicArn;

    @Transactional(rollbackFor = Exception.class)
    public RentalResponseDTO createRental(RentalRequestDTO rentalRequestDTO) {
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

                RentalCreatedEventDTO event = new RentalCreatedEventDTO(
                        rental.getBookId(),
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
        return new RentalResponseDTO(
                rentalRequestDTO.bookIds().stream().map(id -> bookRepository.findByBookId(id).get().getTitle()).toList(),
                rentalRequestDTO.email(),
                rentalRequestDTO.returnDate(),
                LocalDate.now()
        );
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
}
