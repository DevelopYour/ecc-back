package com.seoultech.ecc.repository;

import com.seoultech.ecc.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
