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
	public void tripJoinNotification(long tripId, long userId, String nickname) {
		List<NotificationComponentDto> dtos = getAllTripNotificationComponentByTripId(tripId);
		String content = "'" + dtos.get(0).getTypeName() + "'방에 " + nickname + "님이 참여하였습니다.";
		List<Notification> notifications = tripDtoToNotification(tripId, dtos, content);
		notificationRepository.saveAll(notifications);
	}

	@Transactional
	public void createPlanCreateNotification(long tripId) {
		List<NotificationComponentDto> dtos = getAllTripNotificationComponentByTripId(tripId);
		String content = "'" + dtos.get(0).getTypeName() + "'방에 새 일정이 추가되었습니다.";
		List<Notification> notifications = tripDtoToNotification(tripId, dtos, content);
		notificationRepository.saveAll(notifications);
	}

	@Transactional
	public void createPlanMoveNotification(long tripId) {
		List<NotificationComponentDto> dtos = getAllTripNotificationComponentByTripId(tripId);
		String content = "'" + dtos.get(0).getTypeName() + "'여행 일정 순서가 변경되었습니다.";
		List<Notification> notifications = tripDtoToNotification(tripId, dtos, content);
		notificationRepository.saveAll(notifications);
	}

	private List<Notification> tripDtoToNotification(long tripId, List<NotificationComponentDto> dtos, String content) {
		List<Notification> notifications = new ArrayList<>();

		for (NotificationComponentDto dto : dtos) {
			if (!dto.isPlanActive())
				continue;
			notifications.add(buildNotification(tripId, "trip", dto.getTypeId(), "여행 일정", content,
				dto.getLabelColor(), entityManager.getReference(UserEntity.class, dto.getUserId())));
		}
		return notifications;
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

	public NotificationComponentDto getHistoryNotificationDto(long historyId) {
		return notificationRepository.findHistoryNotificationComponent(historyId);
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

	public void createHistoryReplyNotification(long tripId, long historyId, String nickname, String reply) {
		NotificationComponentDto componentDto = getHistoryNotificationDto(historyId);
		if (!componentDto.isHistoryActive())
			return;

		String content = (nickname.length() > 5 ? nickname.substring(0, 5) : nickname)
						 + "님이 여행자님의 게시물에 댓글을 남겼습니다:" + " \"" +
						 (reply.length() > 10 ? reply.substring(0, 10) : reply)
						 + "\"";

		notificationRepository.save(
			Notification.builder()
				.tripId(tripId)
				.title("여행 기록")
				.content(content)
				.isRead(false)
				.type("history")
				.typeId(historyId)
				.labelColor(componentDto.getLabelColor())
				.user(entityManager.getReference(UserEntity.class, componentDto.getUserId()))
				.build()
		);
	}

	public void createHistoryLikeNotification(long tripId, long historyId, String nickname) {
		NotificationComponentDto componentDto = getHistoryNotificationDto(historyId);
		if (!componentDto.isHistoryActive())
			return;

		String content = (nickname.length() > 5 ? nickname.substring(0, 5) : nickname)
						 + "님이 여행자님의 게시물을 좋아합니다";

		notificationRepository.save(
			Notification.builder()
				.tripId(tripId)
				.title("여행 기록")
				.content(content)
				.isRead(false)
				.type("history")
				.typeId(historyId)
				.labelColor(componentDto.getLabelColor())
				.user(entityManager.getReference(UserEntity.class, componentDto.getUserId()))
				.build()
		);
	}

}
