package com.microsservice.rental_service.service;

import com.microsservice.rental_service.domain.BookEntity;
import com.microsservice.rental_service.dto.BookCreatedEventDTO;
import com.microsservice.rental_service.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public void saveBook(BookCreatedEventDTO event) {
        BookEntity book = new BookEntity();
        book.setBookId(event.bookId());
        book.setTitle(event.title());
        book.setStatus(event.status());
        
        bookRepository.save(book);
        log.info("Livro salvo no rental_service - ID: {}, Título: {}", event.bookId(), event.title());
    }

    @Transactional
    public void updateBookStatus(Long bookId, String status) {
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado: " + bookId));

        book.setStatus(status);
        bookRepository.save(book);
        log.info("Status do livro atualizado - ID: {}, Novo Status: {}", bookId, status);
    }
}
