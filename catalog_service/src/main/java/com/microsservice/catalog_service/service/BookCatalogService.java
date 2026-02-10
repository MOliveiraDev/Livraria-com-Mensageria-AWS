package com.microsservice.catalog_service.service;

import com.microsservice.catalog_service.domain.BookEntity;
import com.microsservice.catalog_service.domain.StockEntity;
import com.microsservice.catalog_service.dto.BookRequestDTO;
import com.microsservice.catalog_service.dto.BookResponseDTO;
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

    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> {
                    StockEntity stock = stockRepository.findByBook(book).orElse(null);
                    return new BookResponseDTO(
                            null,
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
    public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
        try {
            log.info("Criando livro: {}", bookRequestDTO.title());
            

            if (bookRepository.existsByIsbn(bookRequestDTO.isbn())) {
                throw new ISBNWasExistingException("ISBN já existe: " + bookRequestDTO.isbn());
            }
            
            BookEntity book = new BookEntity();
            book.setTitle(bookRequestDTO.title());
            book.setAuthor(bookRequestDTO.author());
            book.setIsbn(bookRequestDTO.isbn());
            book.setStatus(determineBookStatus(bookRequestDTO.quantity()));

            StockEntity stock = new StockEntity();
            stock.setQuantity(bookRequestDTO.quantity());
            stock.setBook(book);

            // Salvar livro e estoque
            BookEntity savedBook = bookRepository.save(book);
            stockRepository.save(stock);
            log.info("Livro criado com sucesso: ID {}", savedBook.getBookId());

            // Enviar mensagem SNS
            String message = String.format("Livro criado - Título: %s, Autor: %s, Estoque: %d", 
                    savedBook.getTitle(), savedBook.getAuthor(), stock.getQuantity());
            log.info("Enviando mensagem SNS: {}", message);
            snsService.sendMessage(livroCriadoTopicArn, message);

            // Verificar estoque baixo
            if (stock.getQuantity() <= 5) {
                String lowStockMessage = String.format("Alerta: Estoque baixo - %s (%d unidades)", 
                        savedBook.getTitle(), stock.getQuantity());
                snsService.sendMessage(livroCriadoTopicArn, lowStockMessage);
            }

            return new BookResponseDTO(
                    null,  
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
    public BookResponseDTO updateBook(Long id, BookRequestDTO bookRequestDTO) {
        try {
            log.info("Atualizando livro ID: {}", id);
            
            BookEntity bookExisting = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Book not found with ID: " + id));

            if (!bookExisting.getIsbn().equals(bookRequestDTO.isbn()) &&
                bookRepository.existsByIsbn(bookRequestDTO.isbn())) {
                throw new ISBNWasExistingException("ISBN já existe em outro livro: " + bookRequestDTO.isbn());
            }

            // Capturar valores antigos para comparação
            Integer oldQuantity = null;
            StockEntity stock = stockRepository.findByBook(bookExisting)
                    .orElseThrow(() -> new RuntimeException("Stock not found for book ID: " + id));
            oldQuantity = stock.getQuantity();

            // Atualizar dados do livro
            bookExisting.setTitle(bookRequestDTO.title());
            bookExisting.setAuthor(bookRequestDTO.author());
            bookExisting.setIsbn(bookRequestDTO.isbn());
            bookExisting.setStatus(determineBookStatus(bookRequestDTO.quantity()));

            // Atualizar estoque
            stock.setQuantity(bookRequestDTO.quantity());

            // Salvar alterações
            BookEntity updatedBook = bookRepository.save(bookExisting);
            stockRepository.save(stock);
            log.info("Livro atualizado com sucesso: ID {}", updatedBook.getBookId());

            // Enviar mensagem SNS
            String message = String.format("Livro atualizado - Título: %s, Autor: %s, Estoque: %d (anterior: %d)", 
                    updatedBook.getTitle(), updatedBook.getAuthor(), stock.getQuantity(), oldQuantity);
            log.info("Enviando mensagem SNS: {}", message);
            snsService.sendMessage(livroAtualizadoTopicArn, message);

            // Verificar estoque baixo
            if (stock.getQuantity() <= 5) {
                String lowStockMessage = String.format("Alerta: Estoque baixo após atualização - %s (%d unidades)", 
                        updatedBook.getTitle(), stock.getQuantity());
                snsService.sendMessage(livroAtualizadoTopicArn, lowStockMessage);
            }

            return new BookResponseDTO(
                    null,  
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


}
