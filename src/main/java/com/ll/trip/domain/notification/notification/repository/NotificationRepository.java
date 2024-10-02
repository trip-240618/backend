package com.ll.trip.domain.notification.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.notification.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
