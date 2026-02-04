package com.microsservice.catalog_service.service;

import com.microsservice.catalog_service.domain.BookEntity;
import com.microsservice.catalog_service.domain.StockEntity;
import com.microsservice.catalog_service.dto.book.CreateBookRequest;
import com.microsservice.catalog_service.dto.book.CreateBookResponse;
import com.microsservice.catalog_service.repository.BookRepository;
import com.microsservice.catalog_service.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final StockRepository stockRepository;


    public CreateBookResponse createBook(CreateBookRequest createBookRequest) {
        BookEntity book = new BookEntity();
        book.setTitle(createBookRequest.title());
        book.setAuthor(createBookRequest.author());
        book.setIsbn(createBookRequest.isbn());

        StockEntity stock = new StockEntity();
        stock.setBook(book);
        stock.setQuantity(createBookRequest.quantity());

        bookRepository.save(book);
        stockRepository.save(stock);

        return new CreateBookResponse("Book created successfully");
    }

    public CreateBookResponse updateBook(Long id, CreateBookRequest createBookRequest){
        BookEntity bookExisting = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        bookExisting.setTitle(createBookRequest.title());
        bookExisting.setAuthor(createBookRequest.author());
        bookExisting.setIsbn(createBookRequest.isbn());
        bookRepository.save(bookExisting);
        return new CreateBookResponse("Book updated successfully");
    }


}
