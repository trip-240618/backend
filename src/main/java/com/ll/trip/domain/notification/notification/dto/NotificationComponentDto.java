package com.ll.trip.domain.notification.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationComponentDto {
	private String fcmToken;
	private long userId; //알림을 읽는 사람
	private long typeId; //히스토리면 히스토리, 트립이면 트립
	private char tripType; //j or p
	private String tripName; //알림에 필요한 내용
	private String labelColor;
	private boolean planActive;
	private boolean historyActive;
	private boolean marketingActive;

}
