package com.microsservice.notification_service.domain;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
public class NotificationEntity {

    @Id
    private String id;

    private String email;

    private String message;

    private LocalDateTime sentAt;

}