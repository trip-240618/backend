package com.ll.trip.domain.plan.plan.dto;

import java.util.List;

import lombok.Data;

@Data
public class PlanCreateRequestDto {
	private String title;
	private String content;
	private List<String> imgUrls;
	//TODO img 개수 제한
}
