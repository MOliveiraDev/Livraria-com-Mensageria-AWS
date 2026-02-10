package com.microsservice.rental_service.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "books_tbl")
public class BookEntity {

    @Id
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "status", nullable = false)
    private String status;
}
