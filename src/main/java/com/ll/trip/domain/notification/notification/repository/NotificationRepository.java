package com.ll.trip.domain.notification.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.notification.firebase.dto.NotificationListDto;
import com.ll.trip.domain.notification.notification.dto.NotificationComponentDto;
import com.ll.trip.domain.notification.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("""
		    select new com.ll.trip.domain.notification.notification.dto.NotificationComponentDto(
			u.fcmToken, u.id, t.id, t.name, t.labelColor, nc.activePlanNotification,
			nc.activeHistoryNotification, nc.activeAdNotification)
			from UserEntity u
			left join Trip t on u.id = :userId and t.id = :tripId
		  	left join u.notificationConfigs nc
		""")
	NotificationComponentDto findNotificationComponentByTripIdAndUserId(long tripId, long userId);

	@Query("""
		    select new com.ll.trip.domain.notification.notification.dto.NotificationComponentDto(
			u.fcmToken, u.id, t.id, t.name, t.labelColor, nc.activePlanNotification,
			nc.activeHistoryNotification, nc.activeAdNotification)
			from UserEntity u
			left join Trip t on u.uuid = :uuid and t.id = :tripId
		  	left join u.notificationConfigs nc
		""")
	NotificationComponentDto findNotificationComponentByTripIdAndUserUuId(long tripId, String uuid);

	@Query("""
		    select new com.ll.trip.domain.notification.notification.dto.NotificationComponentDto(
			u.fcmToken, u.id, t.id, t.name, t.labelColor, nc.activePlanNotification,
			nc.activeHistoryNotification, nc.activeAdNotification)
			from TripMember tm
			left join tm.trip t on tm.trip.id = :tripId
		  	left join tm.user u
		  	left join u.notificationConfigs nc
		""")
	List<NotificationComponentDto> findAllTripNotificationComponentByTripId(long tripId);

	@Query("""
			select new com.ll.trip.domain.notification.firebase.dto.NotificationListDto(
			n.id, n.tripId, n.type, n.typeId, n.title, n.content, n.isRead, n.createDate
			)
			from Notification n
			where n.user.id = :userId and n.createDate >= :weekAgo
		""")
	List<NotificationListDto> findAllByUserIdAndDate(long userId, LocalDateTime weekAgo);

	@Query("""
			select new com.ll.trip.domain.notification.firebase.dto.NotificationListDto(
			n.id, n.tripId, n.type, n.typeId, n.title, n.content, n.isRead, n.createDate
			)
			from Notification n
			where n.user.id = :userId and n.createDate >= :weekAgo and n.title = :title
		""")
	List<NotificationListDto> findAllTypeByUserIdAndDate(long userId, String title, LocalDateTime weekAgo);


}
