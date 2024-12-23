package com.ll.trip.domain.notification.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.notification.notification.dto.NotificationListDto;
import com.ll.trip.domain.notification.notification.dto.NotificationComponentDto;
import com.ll.trip.domain.notification.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("""
		    select new com.ll.trip.domain.notification.notification.dto.NotificationComponentDto(
			u.fcmToken, u.id, t.id, t.type, t.name, t.labelColor, nc.activePlanNotification,
			nc.activeHistoryNotification, nc.activeAdNotification)
			from History h
			inner join h.trip t on h.id = :historyId
			left join h.user u
		  	left join u.notificationConfigs nc
		""")
	NotificationComponentDto findHistoryNotificationComponent(long historyId);

	@Query("""
		    select new com.ll.trip.domain.notification.notification.dto.NotificationComponentDto(
			u.fcmToken, u.id, t.id, t.type, t.name, t.labelColor, nc.activePlanNotification,
			nc.activeHistoryNotification, nc.activeAdNotification)
			from TripMember tm
			inner join tm.trip t on tm.trip.id = :tripId
		  	left join tm.user u
		  	left join u.notificationConfigs nc
		""")
	List<NotificationComponentDto> findAllTripNotificationComponentByTripId(long tripId);

	@Query("""
			select new com.ll.trip.domain.notification.notification.dto.NotificationListDto(
			n.id, t.labelColor, n.destination, n.title, n.content, n.isRead, n.createDate
			)
			from Notification n
			left join n.trip t
			where n.user.id = :userId and n.id < :id and n.createDate >= :weekAgo
			order by n.id desc
		""")
	List<NotificationListDto> findAllByUserIdAndDate(long userId, LocalDateTime weekAgo, long id);

	@Query("""
			select new com.ll.trip.domain.notification.notification.dto.NotificationListDto(
			n.id, t.labelColor, n.destination, n.title, n.content, n.isRead, n.createDate
			)
			from Notification n
			left join n.trip t
			where n.user.id = :userId and n.id < :id and n.createDate >= :weekAgo and n.title = :title 
			order by n.id desc
		""")
	List<NotificationListDto> findAllTypeByUserIdAndDate(long userId, String title, LocalDateTime weekAgo, long id);

	@Modifying
	@Query("""
		update Notification n
		set n.isRead = true
		where n.id = :notificationId and n.user.id = :userId
		""")
	int updateIsReadByIdAndUserID(long notificationId, long userId);

	@Modifying
	@Query("""
		update Notification n
		set n.isRead = true
		where n.user.id = :userId and n.isRead = false
		""")
	int updateAllIsReadByIdAndUserID(long userId);

	int countByUser_IdAndIsRead(long userId, boolean isRead);

	@Modifying
	void deleteByIdAndUserId(long notificationId, long userId);
}

