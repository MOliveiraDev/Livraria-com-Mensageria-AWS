package com.microsservice.catalog_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsAsyncClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsService {

    private final SnsAsyncClient snsAsyncClient;

    public void sendMessage(String topicArn, String message) {
        try {
            snsAsyncClient.publish(builder -> builder.topicArn(topicArn).message(message));
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem SNS: {}", e.getMessage());

        }
    }
}