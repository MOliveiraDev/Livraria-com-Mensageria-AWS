package com.microsservice.catalog_service.repository;

import com.microsservice.catalog_service.domain.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    boolean existsByIsbn(String isbn);
}
