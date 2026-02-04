package com.microsservice.catalog_service.repository;

import com.microsservice.catalog_service.domain.BookEntity;
import com.microsservice.catalog_service.domain.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

    Optional<StockEntity> findByBook(BookEntity book);
}
