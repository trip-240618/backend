package com.ll.trip.domain.notification.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.notification.notification.dto.HistoryNotificationDto;
import com.ll.trip.domain.notification.notification.dto.PlanNotificationDto;
import com.ll.trip.domain.notification.notification.entity.NotificationConfig;

public interface NotificationConfigRepository extends JpaRepository<NotificationConfig, Long> {

	@Modifying
	@Query(
		"""
			update NotificationConfig n
			set n.activeAdNotification = :marketing
			where n.user.id = :userId
		"""
	)
	int updateMarketingAgree(long userId, boolean marketing);

	@Query("""
		select nc
		from NotificationConfig nc
		where nc.user.id = :userId
		""")
	Optional<NotificationConfig> findByUserId(long userId);

	@Query("""
		    select new com.ll.trip.domain.notification.notification.dto.PlanNotificationDto(
			u.fcmToken, u.id, u.nickname, nc.activePlanNotification)
			from TripMember tm
		  	left join tm.user u
		  	left join u.notificationConfigs nc
		  	where tm.trip.id = :tripId
		""")
	List<PlanNotificationDto> findAllPlanNotificationListByTripId(long tripId);

	@Query("""
		    select new com.ll.trip.domain.notification.notification.dto.PlanNotificationDto(
			u.fcmToken, u.id, u.nickname, nc.activeHistoryNotification)
			from TripMember tm
		  	left join tm.user u
		  	left join u.notificationConfigs nc
		  	where tm.trip.id = :tripId
		""")
	List<HistoryNotificationDto> findAllHistoryNotificationListByTripId(long tripId);
}
