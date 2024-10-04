package com.ll.trip.domain.notification.notification.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.notification.notification.dto.NotificationComponentDto;
import com.ll.trip.domain.notification.notification.entity.Notification;
import com.ll.trip.domain.notification.notification.entity.NotificationConfig;
import com.ll.trip.domain.notification.notification.repository.NotificationConfigRepository;
import com.ll.trip.domain.notification.notification.repository.NotificationRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.user.user.entity.UserEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final NotificationConfigRepository notificationConfigRepository;
	private final EntityManager entityManager;

	@Transactional
	public void createNotificationConfig(UserEntity userRef) {
		NotificationConfig config = NotificationConfig.builder()
			.user(userRef)
			.activeHistoryNotification(true)
			.activeAdNotification(true)
			.activePlanNotification(true)
			.build();

		notificationConfigRepository.save(config);
	}

	@Transactional
	public void tripCreateNotifictaion(Trip trip, UserEntity userRef) {
		if (!getNotificationConfig(userRef.getId()).isActivePlanNotification())
			return;
		String content = "'" + trip.getName() + "' 여행방이 생성되었습니다.";

		notificationRepository.save(
			Notification.builder()
				.title("여행 일정")
				.content(content)
				.type("trip")
				.typeValue(trip.getId())
				.isRead(false)
				.type("trip")
				.typeValue(trip.getId())
				.user(userRef)
				.build());
	}

	@Transactional
	public void tripJoinNotifictaion(Trip tripRef, long userId, String nickname) {
		List<NotificationComponentDto> dtos = getAllTripNotificationComponentByTripId(tripRef.getId());
		String content = "'" + dtos.get(0).getTypeName() + "' 여행방에 " + nickname + "님이 참가하였습니다.";
		List<Notification> notifications = new ArrayList<>();

		for (NotificationComponentDto dto : dtos) {
			if(userId == dto.getUserId() || !dto.isPlanActive()) continue;
			notifications.add(
				Notification.builder()
					.title("여행 일정")
					.content(content)
					.user(entityManager.getReference(UserEntity.class, dto.getUserId()))
					.isRead(false)
					.type("trip")
					.typeValue(dto.getTypeId())
					.build());
		}
		notificationRepository.saveAll(notifications);
	}

	public NotificationConfig getNotificationConfig(long userId) {
		return notificationConfigRepository.findByUserId(userId).orElseThrow(NullPointerException::new);
	}

	public List<NotificationComponentDto> getAllTripNotificationComponentByTripId(long tripId) {
		return notificationRepository.findAllTripNotificationComponentByTripId(tripId);
	}
}
