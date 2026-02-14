package com.microsservice.catalog_service.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsservice.catalog_service.dto.BookReturnedReceivedEventDTO;
import com.microsservice.catalog_service.service.BookCatalogService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookReturnedListener {

    private final BookCatalogService bookCatalogService;
    private final ObjectMapper objectMapper;

    @SqsListener("${aws.sqs.livro-retornado-queue}")
    public void listen(String message) {
        try {
            log.info("Mensagem de devolução recebida: {}", message);
            JsonNode rootNode = objectMapper.readTree(message);
            String eventMessage = rootNode.get("Message").asText();
            BookReturnedReceivedEventDTO event = objectMapper.readValue(eventMessage, BookReturnedReceivedEventDTO.class);
            log.info("Evento de devolução processado: {}", event);
            bookCatalogService.returnBookToStock(event.bookId());
        } catch (Exception e) {
            log.error("Erro ao processar devolução: {}", e.getMessage());
        }
    }
}
