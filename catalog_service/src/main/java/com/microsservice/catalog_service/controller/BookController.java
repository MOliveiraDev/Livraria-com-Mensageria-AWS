package com.microsservice.catalog_service.controller;

import com.microsservice.catalog_service.dto.BookRequestDTO;
import com.microsservice.catalog_service.dto.BookResponseDTO;
import com.microsservice.catalog_service.service.BookCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookCatalogService bookCatalogService;

    @GetMapping("/getBooks")
    public ResponseEntity<List<BookResponseDTO>> getBooks() {
        return ResponseEntity.ok(bookCatalogService.getAllBooks());
    }

    @PostMapping("/createBook")
    public ResponseEntity<BookResponseDTO> createBook(@Valid BookRequestDTO bookRequestDTO) {
        return ResponseEntity.ok(bookCatalogService.createBook(bookRequestDTO));
    }

    @PutMapping("/updateBook")
    public ResponseEntity<BookResponseDTO> updateBook(@RequestParam("id")  Long id, @Valid BookRequestDTO bookRequestDTO) {
        return ResponseEntity.ok(bookCatalogService.updateBook(id, bookRequestDTO));
    }
}
