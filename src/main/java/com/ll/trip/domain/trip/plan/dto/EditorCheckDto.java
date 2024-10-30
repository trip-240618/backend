package com.ll.trip.domain.trip.plan.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditorCheckDto {
	private Map<String, String> sessionIdMap;
	private Map<String, String[]> destinationMap;
}
