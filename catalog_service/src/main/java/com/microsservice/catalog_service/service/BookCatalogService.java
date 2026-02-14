package com.microsservice.catalog_service.service;

import com.microsservice.catalog_service.domain.BookEntity;
import com.microsservice.catalog_service.domain.StockEntity;
import com.microsservice.catalog_service.dto.BookCatalogRequestDTO;
import com.microsservice.catalog_service.dto.BookCatalogResponseDTO;
import com.microsservice.catalog_service.dto.BookCreatedEventDTO;
import com.microsservice.catalog_service.dto.BookUpdatedEventDTO;
import com.microsservice.catalog_service.enums.BookStatus;
import com.microsservice.catalog_service.exception.book.ISBNWasExistingException;
import com.microsservice.catalog_service.repository.BookRepository;
import com.microsservice.catalog_service.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookCatalogService {

    private final BookRepository bookRepository;
    private final StockRepository stockRepository;
    private final SnsService snsService;

    @Value("${aws.sns.livro-criado-topic-arn}")
    private String livroCriadoTopicArn;

    @Value("${aws.sns.livro-atualizado-topic-arn}")
    private String livroAtualizadoTopicArn;

    public List<BookCatalogResponseDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> {
                    StockEntity stock = stockRepository.findByBook(book).orElse(null);
                    return new BookCatalogResponseDTO(
                            book.getBookId(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getIsbn(),
                            book.getStatus(),
                            stock != null ? stock.getQuantity() : 0,
                            "Book retrieved successfully"
                    );
                })
                .toList();
    }


    @Transactional(rollbackFor = Exception.class)
    public BookCatalogResponseDTO createBook(BookCatalogRequestDTO bookCatalogRequestDTO) {
        try {
            log.info("Criando livro: {}", bookCatalogRequestDTO.title());


            if (bookRepository.existsByIsbn(bookCatalogRequestDTO.isbn())) {
                throw new ISBNWasExistingException("ISBN já existe: " + bookCatalogRequestDTO.isbn());
            }

            BookEntity book = new BookEntity();
            book.setTitle(bookCatalogRequestDTO.title());
            book.setAuthor(bookCatalogRequestDTO.author());
            book.setIsbn(bookCatalogRequestDTO.isbn());
            book.setStatus(determineBookStatus(bookCatalogRequestDTO.quantity()));

            StockEntity stock = new StockEntity();
            stock.setQuantity(bookCatalogRequestDTO.quantity());
            stock.setBook(book);

            // Salvar livro e estoque
            BookEntity savedBook = bookRepository.save(book);
            stockRepository.save(stock);
            log.info("Livro criado com sucesso: ID {}", savedBook.getBookId());

            // Enviar evento estruturado SNS
            BookCreatedEventDTO event = new BookCreatedEventDTO(
                    savedBook.getBookId(),
                    savedBook.getTitle(),
                    savedBook.getStatus()
            );
            snsService.sendEvent(livroCriadoTopicArn, event);

            return new BookCatalogResponseDTO(
                    savedBook.getBookId(),
                    savedBook.getTitle(),
                    savedBook.getAuthor(),
                    savedBook.getIsbn(),
                    savedBook.getStatus(),
                    stock.getQuantity(),
                    "Book created successfully"
            );
        } catch (Exception e) {
            log.error("Erro ao criar livro: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public BookCatalogResponseDTO updateBook(Long id, BookCatalogRequestDTO bookCatalogRequestDTO) {
        try {
            log.info("Atualizando livro ID: {}", id);

            BookEntity bookExisting = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            if (!bookExisting.getIsbn().equals(bookCatalogRequestDTO.isbn()) &&
                    bookRepository.existsByIsbn(bookCatalogRequestDTO.isbn())) {
                throw new ISBNWasExistingException("ISBN já existe em outro livro: " + bookCatalogRequestDTO.isbn());
            }

            StockEntity stock = stockRepository.findByBook(bookExisting)
                    .orElseThrow(() -> new RuntimeException("Stock not found"));


            // Atualizar dados do livro
            bookExisting.setTitle(bookCatalogRequestDTO.title());
            bookExisting.setAuthor(bookCatalogRequestDTO.author());
            bookExisting.setIsbn(bookCatalogRequestDTO.isbn());
            bookExisting.setStatus(determineBookStatus(bookCatalogRequestDTO.quantity()));

            // Atualizar estoque
            stock.setQuantity(bookCatalogRequestDTO.quantity());

            // Salvar alterações
            BookEntity updatedBook = bookRepository.save(bookExisting);
            stockRepository.save(stock);
            log.info("Livro atualizado com sucesso: ID {}", updatedBook.getBookId());

            // Enviar evento estruturado SNS
            BookUpdatedEventDTO event = new BookUpdatedEventDTO(
                    updatedBook.getBookId(),
                    updatedBook.getTitle(),
                    updatedBook.getStatus()
            );
            log.info("Enviando evento SNS: {}", event);
            snsService.sendEvent(livroAtualizadoTopicArn, event);

            return new BookCatalogResponseDTO(
                    updatedBook.getBookId(),
                    updatedBook.getTitle(),
                    updatedBook.getAuthor(),
                    updatedBook.getIsbn(),
                    updatedBook.getStatus(),
                    stock.getQuantity(),
                    "Book updated successfully"
            );
        } catch (Exception e) {
            log.error("Erro ao atualizar livro ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    private BookStatus determineBookStatus(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return BookStatus.UNAVAILABLE;
        }
        return BookStatus.AVAILABLE;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStockOnRental(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

        StockEntity stock = stockRepository.findByBook(book)
                .orElseThrow(() -> new RuntimeException("Stock not found for book ID: " + bookId));

        if (stock.getQuantity() <= 0) {
            throw new IllegalArgumentException("No stock available for book ID: " + bookId);
        }

        stock.setQuantity(stock.getQuantity() - 1);
        book.setStatus(determineBookStatus(stock.getQuantity()));

        stockRepository.save(stock);
        bookRepository.save(book);
        log.info("Estoque atualizado para livro ID {}: nova quantidade = {}", bookId, stock.getQuantity());

        BookUpdatedEventDTO event = new BookUpdatedEventDTO(
                book.getBookId(),
                book.getTitle(),
                book.getStatus()
        );
        log.info("Enviando evento SNS após atualização de estoque: {}", event);
        snsService.sendEvent(livroAtualizadoTopicArn, event);
    }

    @Transactional(rollbackFor = Exception.class)
    public void returnBookToStock(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

        StockEntity stock = stockRepository.findByBook(book)
                .orElseThrow(() -> new RuntimeException("Stock not found for book ID: " + bookId));

        stock.setQuantity(stock.getQuantity() + 1);
        book.setStatus(determineBookStatus(stock.getQuantity()));

        stockRepository.save(stock);
        bookRepository.save(book);
        log.info("Livro devolvido - Estoque atualizado para livro ID {}: nova quantidade = {}", bookId, stock.getQuantity());

        BookUpdatedEventDTO event = new BookUpdatedEventDTO(
                book.getBookId(),
                book.getTitle(),
                book.getStatus()
        );
        snsService.sendEvent(livroAtualizadoTopicArn, event);
    }
}
