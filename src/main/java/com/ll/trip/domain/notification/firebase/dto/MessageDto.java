package com.ll.trip.domain.notification.firebase.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class MessageDto {
	@Builder.Default
	private List<String> tokenList = new ArrayList<>();

	private String title;

	private String body;

	@Builder.Default
	private Map<String, String> data = new HashMap<>();

	public MessageDto(List<String> tokenList, String title, String body, Map<String, String> data) {
		this.tokenList = tokenList;
		this.title = title;
		this.body = body;
		this.data = data;
	}
}
