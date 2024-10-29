package com.ll.trip.domain.notification.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

	NotificationConfig findByUser_Id(long userId);

	@Modifying
	@Query("""
		update NotificationConfig n
		set n.activePlanNotification = :activePlan,
		n.activeHistoryNotification = :activeLikeReply,
		n.activeAdNotification = :activeMarketing
		where n.user.id = :userId
		""")
	void updateConfig(long userId, boolean activePlan, boolean activeLikeReply, boolean activeMarketing);
}
