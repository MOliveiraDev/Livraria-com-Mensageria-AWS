package com.microsservice.rental_service.repository;

import com.microsservice.rental_service.domain.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    Optional<BookEntity> findByBookId(Long bookId);
}
