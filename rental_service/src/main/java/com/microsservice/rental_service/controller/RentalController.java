package com.microsservice.rental_service.controller;

import com.microsservice.rental_service.dto.RentalRequestDTO;
import com.microsservice.rental_service.dto.RentalResponseDTO;
import com.microsservice.rental_service.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping("/getRentals")
    public ResponseEntity<List<RentalResponseDTO>> getRentals(String email) {
        return ResponseEntity.ok(rentalService.getRentalsByEmail(email));
    }

    @PostMapping("/createRental")
    public ResponseEntity<RentalResponseDTO> createRental(RentalRequestDTO rentalRequestDTO) {
        return ResponseEntity.ok(rentalService.createRental(rentalRequestDTO));
    }
}
