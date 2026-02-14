package com.microsservice.notification_service.repository;

import com.microsservice.notification_service.domain.NotificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<NotificationEntity, String> {
}
