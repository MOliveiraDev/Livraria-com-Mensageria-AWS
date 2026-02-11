package com.microsservice.rental_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsAsyncClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsService {

    private final SnsAsyncClient snsAsyncClient;
    private final ObjectMapper objectMapper;

    public void sendEvent(String topicArn, Object event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            snsAsyncClient.publish(builder -> builder.topicArn(topicArn).message(message));
        } catch (Exception e) {
            log.error("Erro ao enviar evento SNS: {}", e.getMessage());
        }
    }
}