package com.ll.trip.domain.alarm.alarm.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ll.trip.domain.alarm.alarm.service.AlarmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfiguration {
	private final AlarmService alarmService;

	@Scheduled(fixedDelay = 60000)
	public void sendAlarm() {
		alarmService.sendAlarm();
	}

}
