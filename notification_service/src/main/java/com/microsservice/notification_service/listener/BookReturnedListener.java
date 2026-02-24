package com.microsservice.notification_service.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsservice.notification_service.dto.BookReturnedEventDTO;
import com.microsservice.notification_service.service.EmailService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookReturnedListener {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @SqsListener("${aws.sqs.livro-retornado-queue}")
    public void listen(String message) {
        try {
            log.info("Mensagem de devolução recebida: {}", message);

            JsonNode rootNode = objectMapper.readTree(message);
            String eventMessage = rootNode.get("Message").asText();

            BookReturnedEventDTO event = objectMapper.readValue(eventMessage, BookReturnedEventDTO.class);
            log.info("Evento de devolução processado: {}", event);

            emailService.sendBookReturnedConfirmation(event.email(), event.bookTitle());
        } catch (Exception e) {
            log.error("Erro ao processar mensagem de devolução: {}", e.getMessage());
        }
    }
}
