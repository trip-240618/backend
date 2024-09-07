package com.ll.trip.domain.alarm.alarm.service;

import org.springframework.stereotype.Service;

import com.ll.trip.domain.alarm.alarm.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {
	private final NotificationRepository notificationRepository;
	public void sendAlarm() {

	}
}
