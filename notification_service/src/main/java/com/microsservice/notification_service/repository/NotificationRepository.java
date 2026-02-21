package com.microsservice.notification_service.repository;

import com.microsservice.notification_service.domain.NotificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<NotificationEntity, String> {
}