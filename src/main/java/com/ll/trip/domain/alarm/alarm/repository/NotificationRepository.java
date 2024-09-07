package com.ll.trip.domain.alarm.alarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.alarm.alarm.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
