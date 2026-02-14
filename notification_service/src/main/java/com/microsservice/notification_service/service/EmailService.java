package com.microsservice.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendRentalConfirmation(String bookTitle, String email, String returnDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Confirmação de Aluguel de Livro");
            message.setText("Olá,\n\n" +
                    "Seu aluguel foi confirmado com sucesso!\n\n" +
                    "Livro: " + bookTitle + "\n" +
                    "Data de Devolução: " + returnDate + "\n\n" +
                    "Obrigado por usar nosso serviço!");

            mailSender.send(message);
            log.info("E-mail enviado com sucesso para: {}", email);
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail para {}: {}", email, e.getMessage());
        }
    }

    public void sendRentalReminder(String email, String reminderMessage, String returnDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Lembrete de Devolução de Livro");
            message.setText("Olá,\n\n" +
                    reminderMessage + "\n\n" +
                    "Data de Devolução: " + returnDate + "\n\n" +
                    "Por favor, não se esqueça de devolver o livro no prazo.\n\n" +
                    "Obrigado!");

            mailSender.send(message);
            log.info("Lembrete enviado com sucesso para: {}", email);
        } catch (Exception e) {
            log.error("Erro ao enviar lembrete para {}: {}", email, e.getMessage());
        }
    }
}
