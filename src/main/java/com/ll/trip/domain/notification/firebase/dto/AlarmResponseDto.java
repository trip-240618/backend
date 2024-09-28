package com.ll.trip.domain.notification.firebase.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

//모바일에서 전달받을 객체
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmResponseDto {
	private String token;

	private String title;

	private String body;

	@Builder(toBuilder = true)
	public AlarmResponseDto(String token, String title, String body) {
		this.token = token;
		this.title = title;
		this.body = body;
	}
}
