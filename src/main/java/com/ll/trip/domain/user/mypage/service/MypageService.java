package com.ll.trip.domain.user.mypage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.user.mypage.entity.NotificationConfig;
import com.ll.trip.domain.user.mypage.repository.NotificationConfigRepository;
import com.ll.trip.domain.user.user.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageService {
	private final NotificationConfigRepository notificationConfigRepository;

	@Transactional
	public void createNotificationConfig(UserEntity user) {
		NotificationConfig config = NotificationConfig.builder()
			.user(user)
			.activeReplyNotification(true)
			.activeAdNotification(true)
			.activePlanNotification(true)
			.build();

		notificationConfigRepository.save(config);
	}
}
