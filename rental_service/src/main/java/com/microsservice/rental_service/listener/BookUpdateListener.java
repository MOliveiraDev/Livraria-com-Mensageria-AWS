package com.microsservice.rental_service.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsservice.rental_service.dto.BookCreatedEventDTO;
import com.microsservice.rental_service.service.BookService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookUpdateListener {

    private final BookService bookService;
    private final ObjectMapper objectMapper;

    @SqsListener("${aws.sqs.book-updated-queue}")
    public void listen(String message) {
        try {
            log.info("Mensagem recebida: {}", message);
            
            JsonNode rootNode = objectMapper.readTree(message);
            String snsMessage = rootNode.get("Message").asText();
            
            BookCreatedEventDTO event = objectMapper.readValue(snsMessage, BookCreatedEventDTO.class);
            bookService.updateBookStatus(event);
            
        } catch (Exception e) {
            log.error("Erro ao processar mensagem: {}", e.getMessage(), e);
        }
    }
}
