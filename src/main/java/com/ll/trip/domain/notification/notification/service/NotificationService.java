package com.ll.trip.domain.notification.notification.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.notification.firebase.dto.NotificationListDto;
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
	public void tripJoinNotification(Trip tripRef, long userId, String nickname) {
		List<NotificationComponentDto> dtos = getAllTripNotificationComponentByTripId(tripRef.getId());
		String content = "'" + dtos.get(0).getTypeName() + "' 여행방에 " + nickname + "님이 참여하였습니다.";
		List<Notification> notifications = new ArrayList<>();

		for (NotificationComponentDto dto : dtos) {
			if (!dto.isPlanActive())
				continue;
			notifications.add(buildNotification(tripRef.getId(), "trip", dto.getTypeId(), "여행 일정", content,
				dto.getLabelColor(), entityManager.getReference(UserEntity.class, dto.getUserId())));
		}
		notificationRepository.saveAll(notifications);
	}

	@Transactional
	public Notification buildAndSaveNotification(Long tripId, String type, Long typeId, String title, String content,
		String labelColor, UserEntity userRef) {
		return notificationRepository.save(
			buildNotification(tripId, type, typeId, title, content, labelColor, userRef));
	}

	public Notification buildNotification(Long tripId, String type, Long typeId, String title, String content,
		String labelColor, UserEntity userRef) {
		return Notification.builder()
			.title(title)
			.content(content)
			.user(userRef)
			.isRead(false)
			.type(type)
			.labelColor(labelColor)
			.tripId(tripId)
			.typeId(typeId)
			.build();
	}

	public List<NotificationComponentDto> getAllTripNotificationComponentByTripId(long tripId) {
		return notificationRepository.findAllTripNotificationComponentByTripId(tripId);
	}

	public NotificationComponentDto getNotificationComponentByTripIdAndUserId(long tripId, long userId) {
		return notificationRepository.findNotificationComponentByTripIdAndUserId(tripId, userId);
	}

	public NotificationComponentDto getNotificationComponentByTripIdAndUserUuid(long tripId, String uuid) {
		return notificationRepository.findNotificationComponentByTripIdAndUserUuId(tripId, uuid);
	}

	public List<NotificationListDto> getListByUserIdAndTitle(long userId, String title) {
		return title == null ?
			notificationRepository.findAllByUserIdAndDate(userId, LocalDateTime.now().minusDays(7))
			: notificationRepository.findAllTypeByUserIdAndDate(userId, title, LocalDateTime.now().minusDays(7));
	}

	@Transactional
	public void userCreateNotification(UserEntity userRef) {
		Notification notification = buildAndSaveNotification(null, "app", null, "트립스토리", "트립스토리 회원 가입을 환영합니다 :)",
			"212121", userRef);
	}

	@Transactional
	public void updateIsReadByIdAndUserId(long notificationId, long userId) {
		if (notificationRepository.updateIsReadByIdAndUserID(notificationId, userId) == 0)
			throw new NoSuchElementException("일치하는 알림이 없습니다.");
	}

	@Transactional
	public void updateAllIsReadByUserId(long userId) {
		if (notificationRepository.updateAllIsReadByIdAndUserID(userId) == 0)
			throw new NoSuchElementException("일치하는 알림이 없습니다.");
	}
}
