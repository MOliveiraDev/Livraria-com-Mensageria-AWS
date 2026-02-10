package com.microsservice.rental_service.repository;

import com.microsservice.rental_service.domain.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
}
