package com.ll.trip.domain.notification.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryNotificationDto {
	private String fcmToken;
	private String userId;
	private String nickname;
	private boolean active;
}
