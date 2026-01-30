package com.microsservice.catalog_service.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "stock_tbl")
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long stockId;

    @JoinColumn(name = "book_id")
    @ManyToOne
    private BookEntity book;

    @Column(name = "quantity")
    private Integer quantity;
}
