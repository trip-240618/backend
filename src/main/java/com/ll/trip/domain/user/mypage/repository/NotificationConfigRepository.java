package com.ll.trip.domain.user.mypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.user.mypage.entity.NotificationConfig;

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
}
