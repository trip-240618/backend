package com.ll.trip.domain.plan.plan.dto;

import com.ll.trip.domain.plan.plan.entity.Plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanCreateResponseDto {
	private Long idx;
	private String title;
	private String content;

	public PlanCreateResponseDto(Plan plan) {
		this.idx = plan.getIdx();
		this.title = plan.getTitle();
		this.content = plan.getContent();
	}
}
