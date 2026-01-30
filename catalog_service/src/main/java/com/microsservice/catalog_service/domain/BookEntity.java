package com.microsservice.catalog_service.domain;

import com.microsservice.catalog_service.enums.BookStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "books_tbl")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long bookId;

    @Column(name = "title", nullable = false, length = 200)
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Column(name = "author", nullable = false, length = 100)
    @NotBlank(message = "Author is mandatory")
    private String author;

    @Column(name = "isbn", unique = true, nullable = false, length = 20)
    @NotBlank(message = "ISBN is mandatory")
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookStatus status;

}
