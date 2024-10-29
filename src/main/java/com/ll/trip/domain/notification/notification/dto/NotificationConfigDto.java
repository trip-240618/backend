package com.ll.trip.domain.notification.notification.dto;

import com.ll.trip.domain.notification.notification.entity.NotificationConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationConfigDto {
	@Schema(description = "일정 알림")
	private final boolean activePlan;

	@Schema(description = "좋아요, 댓글 알림")
	private final boolean activeLikeReply;

	@Schema(description = "마케팅 알림")
	private final boolean activeMarketing;

	public NotificationConfigDto(NotificationConfig config) {
		this.activePlan = config.isActivePlanNotification();
		this.activeLikeReply = config.isActiveHistoryNotification();
		this.activeMarketing = config.isActiveAdNotification();
	}
}
