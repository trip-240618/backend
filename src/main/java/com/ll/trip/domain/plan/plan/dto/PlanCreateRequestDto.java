package com.ll.trip.domain.plan.plan.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanCreateRequestDto {
	private String title;
	private String content;
	private List<String> ImageUris;
}
