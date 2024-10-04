package com.ll.trip.domain.notification.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.notification.notification.dto.NotificationComponentDto;
import com.ll.trip.domain.notification.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	@Query("""
		    select new com.ll.trip.domain.notification.notification.dto.NotificationComponentDto(
			u.fcmToken, u.id, t.id, t.name, nc.activePlanNotification,
			nc.activeHistoryNotification, nc.activeAdNotification)
			from TripMember tm
			left join tm.trip t on tm.trip.id = :tripId
		  	left join tm.user u
		  	left join u.notificationConfigs nc
		""")
	List<NotificationComponentDto> findAllTripNotificationComponentByTripId(long tripId);
}
