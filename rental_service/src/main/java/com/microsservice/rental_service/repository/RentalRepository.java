package com.microsservice.rental_service.repository;

import com.microsservice.rental_service.domain.RentalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<RentalEntity, Long> {
    List<RentalEntity> findByEmail(String email);
}
