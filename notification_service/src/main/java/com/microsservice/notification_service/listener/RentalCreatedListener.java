package com.microsservice.notification_service.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsservice.notification_service.dto.RentalRecivedEventDTO;
import com.microsservice.notification_service.service.EmailService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalCreatedListener {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @SqsListener("${aws.sqs.livro-alugado-queue}")
    public void listen(String message) {
        try {
            log.info("Mensagem recebida: {}", message);

            JsonNode rootNode = objectMapper.readTree(message);
            String eventMessage = rootNode.get("Message").asText();

            RentalRecivedEventDTO event = objectMapper.readValue(eventMessage, RentalRecivedEventDTO.class);
            log.info("Evento processado: {}", event);
            
            emailService.sendRentalConfirmation(
                event.bookTitle(),
                event.email(),
                event.returnDate()
            );
        } catch (Exception e) {
            log.error("Erro ao processar mensagem: {}", e.getMessage());
        }
    }
}
