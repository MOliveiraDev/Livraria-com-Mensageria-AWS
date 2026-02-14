package com.microsservice.notification_service.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsservice.notification_service.dto.RentalReminderCreatedEventDTO;
import com.microsservice.notification_service.service.EmailService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalReminderListener {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @SqsListener("${aws.sqs.rental-reminder-queue}")
    public void listen(String message) {
        try {
            log.info("Mensagem de lembrete recebida: {}", message);

            JsonNode rootNode = objectMapper.readTree(message);
            String eventMessage = rootNode.get("Message").asText();

            RentalReminderCreatedEventDTO event = objectMapper.readValue(eventMessage, RentalReminderCreatedEventDTO.class);
            log.info("Evento de lembrete processado: {}", event);

            emailService.sendRentalReminder(event.email(), event.message(), event.returnDate());
        } catch (Exception e) {
            log.error("Erro ao processar mensagem de lembrete: {}", e.getMessage());
        }
    }
}
