package com.microsservice.catalog_service.controller;

import com.microsservice.catalog_service.dto.BookCatalogRequestDTO;
import com.microsservice.catalog_service.dto.BookCatalogResponseDTO;
import com.microsservice.catalog_service.service.BookCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookCatalogController {

    private final BookCatalogService bookCatalogService;

    @GetMapping("/getBooks")
    public ResponseEntity<List<BookCatalogResponseDTO>> getBooks() {
        return ResponseEntity.ok(bookCatalogService.getAllBooks());
    }

    @PostMapping("/createBook")
    public ResponseEntity<BookCatalogResponseDTO> createBook(@RequestBody @Valid BookCatalogRequestDTO bookCatalogRequestDTO) {
        return ResponseEntity.ok(bookCatalogService.createBook(bookCatalogRequestDTO));
    }

    @PutMapping("/updateBook")
    public ResponseEntity<BookCatalogResponseDTO> updateBook(@RequestParam("id")  Long id, @RequestBody @Valid BookCatalogRequestDTO bookCatalogRequestDTO) {
        return ResponseEntity.ok(bookCatalogService.updateBook(id, bookCatalogRequestDTO));
    }
}
