package com.ll.trip.domain.alarm.firebase.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FcmMessageDto {

	private Message message;
	private boolean validateOnly;

	@lombok.Data
	@Builder
	public static class Message {
		private List<String> tokens;
		private Notification notification;
		private Data data;
	}

	@lombok.Data
	@Builder
	public static class Notification {
		private String title;
		private String body;
	}

	@lombok.Data
	@Builder
	public static class Data {
		private String key1;
		private String key2;
	}
}
