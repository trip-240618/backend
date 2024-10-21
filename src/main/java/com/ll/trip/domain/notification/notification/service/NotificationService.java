package com.ll.trip.domain.notification.notification.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.notification.firebase.service.FcmMessageUtil;
import com.ll.trip.domain.notification.notification.dto.NotificationComponentDto;
import com.ll.trip.domain.notification.notification.dto.NotificationListDto;
import com.ll.trip.domain.notification.notification.entity.Notification;
import com.ll.trip.domain.notification.notification.entity.NotificationConfig;
import com.ll.trip.domain.notification.notification.repository.NotificationConfigRepository;
import com.ll.trip.domain.notification.notification.repository.NotificationRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.global.handler.exception.NoSuchDataException;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final NotificationConfigRepository notificationConfigRepository;
	private final EntityManager entityManager;
	private final FcmMessageUtil fcmMessageUtil;

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
		String content = "'" + dtos.get(0).getTripName() + "'방에 " + nickname + "님이 참여하였습니다.";
		String destination = "/trip/plan?tripId=" + tripId + "&type=" + dtos.get(0).getTripType();
		List<Notification> notifications = tripDtoToNotification(tripId, dtos, content, destination);
		notificationRepository.saveAll(notifications);
	}

	@Transactional
	public void createPlanCreateNotification(long tripId) {
		List<NotificationComponentDto> dtos = getAllTripNotificationComponentByTripId(tripId);
		String content = "'" + dtos.get(0).getTripName() + "'방에 새 일정이 추가되었습니다.";
		String destination = "/trip/plan?tripId=" + tripId + "&type=" + dtos.get(0).getTripType();
		List<Notification> notifications = tripDtoToNotification(tripId, dtos, content, destination);
		notificationRepository.saveAll(notifications);
	}

	@Transactional
	public void createPlanMoveNotification(long tripId) {
		List<NotificationComponentDto> dtos = getAllTripNotificationComponentByTripId(tripId);
		String content = "'" + dtos.get(0).getTripName() + "'여행 일정 순서가 변경되었습니다.";
		String destination = "/trip/plan?tripId=" + tripId + "&type=" + dtos.get(0).getTripType();
		List<Notification> notifications = tripDtoToNotification(tripId, dtos, content, destination);
		notificationRepository.saveAll(notifications);
	}

	private List<Notification> tripDtoToNotification(long tripId, List<NotificationComponentDto> dtos, String content,
		String destination) {
		List<Notification> notifications = new ArrayList<>();
		Trip tripRef = entityManager.getReference(Trip.class, tripId);
		String title = "여행 일정";

		List<String> tokenList = new ArrayList<>();
		for (NotificationComponentDto dto : dtos) {
			if (!dto.isPlanActive())
				continue;
			UserEntity userRef = entityManager.getReference(UserEntity.class, dto.getUserId());
			notifications.add(
				buildNotification(tripRef, userRef, destination, title, content)
			);
			if (dto.getFcmToken() != null)
				tokenList.add(dto.getFcmToken());
		}

		fcmMessageUtil.sendMessage(tokenList, title, content, Map.of("destination", destination));
		return notifications;
	}

	public Notification buildNotification(Trip trip, UserEntity userRef, String destination, String title,
		String content) {
		return Notification.builder()
			.title(title)
			.content(content)
			.user(userRef)
			.trip(trip)
			.isRead(false)
			.destination(destination)
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
	public void updateIsReadByIdAndUserId(long notificationId, long userId) {
		if (notificationRepository.updateIsReadByIdAndUserID(notificationId, userId) == 0)
			throw new NoSuchElementException(
				"알림수정이 이루어지지 않았습니다. notificationId: " + notificationId + ", userId: " + userId);
	}

	@Transactional
	public void updateAllIsReadByUserId(long userId) {
		if (notificationRepository.updateAllIsReadByIdAndUserID(userId) == 0)
			throw new NoSuchDataException("알림수정이 이루어지지 않았습니다. userId: " + userId);
	}

	@Transactional
	public void createHistoryReplyNotification(long tripId, long historyId, long userId, String nickname,
		String reply) {
		NotificationComponentDto componentDto = getHistoryNotificationDto(historyId);
		if (componentDto.getUserId() == userId || !componentDto.isHistoryActive())
			return;
		String title = "여행 기록";
		String content = nickname + "님이 여행자님의 게시물에 댓글을 남겼습니다:" + " \"" +
						 (reply.length() > 10 ? reply.substring(0, 10) : reply)
						 + "\"";
		String destination = "/trip/history?tripId=" + tripId + "&historyId=" + historyId;

		if (!componentDto.getFcmToken().isBlank())
			fcmMessageUtil.sendMessage(List.of(componentDto.getFcmToken()), title, content,
				Map.of("destination", destination));

		notificationRepository.save(
			Notification.builder()
				.trip(entityManager.getReference(Trip.class, tripId))
				.user(entityManager.getReference(UserEntity.class, componentDto.getUserId()))
				.title(title)
				.content(content)
				.isRead(false)
				.destination(destination)
				.build()
		);
	}

	@Transactional
	public void createHistoryLikeNotification(long tripId, long historyId, long userId, String nickname) {
		NotificationComponentDto componentDto = getHistoryNotificationDto(historyId);
		if (!componentDto.isHistoryActive() || userId == componentDto.getUserId())
			return;
		log.info("userId: " + componentDto.getUserId());
		String content = nickname + "님이 여행자님의 게시물을 좋아합니다";
		String destination = "/trip/history?tripId=" + tripId + "&historyId=" + historyId;

		notificationRepository.save(
			Notification.builder()
				.trip(entityManager.getReference(Trip.class, tripId))
				.user(entityManager.getReference(UserEntity.class, componentDto.getUserId()))
				.title("여행 기록")
				.content(content)
				.isRead(false)
				.destination(destination)
				.build()
		);
	}

	public long countUnReadByUserId(long userId) {
		return notificationRepository.countByUser_IdAndIsRead(userId, false);
	}
}
