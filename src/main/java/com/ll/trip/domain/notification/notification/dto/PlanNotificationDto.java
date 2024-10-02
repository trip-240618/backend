package com.ll.trip.domain.notification.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanNotificationDto {
	private String fcmToken;
	private long userId;
	private String nickname;
	private boolean active;
}
