package com.microsservice.rental_service.config.scheduler;

import com.microsservice.rental_service.domain.RentalEntity;
import com.microsservice.rental_service.repository.RentalRepository;
import com.microsservice.rental_service.service.SnsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalReminderScheduler {

    private final RentalRepository rentalRepository;
    private final SnsService snsService;

    @Value("${aws.sns.rental-reminder-topic-arn:arn:aws:sns:us-east-2:${AWS_ACCOUNT_ID}:rental-reminder-topic}")
    private String rentalReminderTopicArn;

    @Value("${aws.sns.book-returned-topic-arn:arn:aws:sns:us-east-2:${AWS_ACCOUNT_ID}:book-returned-topic}")
    private String bookReturnedTopicArn;

    @Scheduled(cron = "0 0 9 * * *")
    public void checkRentalReminders() {
        log.info("Verificando lembretes de devolução...");
        
        List<RentalEntity> rentals = rentalRepository.findAll();
        LocalDate today = LocalDate.now();

        for (RentalEntity rental : rentals) {
            long daysUntilReturn = ChronoUnit.DAYS.between(today, rental.getReturnDate());

            if (daysUntilReturn == 7) {
                sendReminder(rental, "Faltam 7 dias para devolver o livro!");
            } else if (daysUntilReturn == 4) {
                sendReminder(rental, "Faltam 4 dias para devolver o livro!");
            } else if (daysUntilReturn == 1) {
                sendReminder(rental, "Falta apenas 1 dia para devolver o livro!");
            } else if (daysUntilReturn == 0) {
                sendReminder(rental, "Hoje é o dia de devolver o livro!");
                sendBookReturnedEvent(rental.getBookId());
                rentalRepository.delete(rental);
                log.info("Aluguel ID {} removido - data de devolução atingida", rental.getRentalId());
            }
        }
    }

    private void sendReminder(RentalEntity rental, String message) {
        try {
            String event = String.format("{\"email\":\"%s\",\"bookId\":%d,\"message\":\"%s\",\"returnDate\":\"%s\"}",
                    rental.getEmail(), rental.getBookId(), message, rental.getReturnDate());
            snsService.sendEvent(rentalReminderTopicArn, event);
            log.info("Lembrete enviado para {}: {}", rental.getEmail(), message);
        } catch (Exception e) {
            log.error("Erro ao enviar lembrete: {}", e.getMessage());
        }
    }

    private void sendBookReturnedEvent(Long bookId) {
        try {
            String event = String.format("{\"bookId\":%d}", bookId);
            snsService.sendEvent(bookReturnedTopicArn, event);
            log.info("Evento de devolução enviado para bookId: {}", bookId);
        } catch (Exception e) {
            log.error("Erro ao enviar evento de devolução: {}", e.getMessage());
        }
    }
}
