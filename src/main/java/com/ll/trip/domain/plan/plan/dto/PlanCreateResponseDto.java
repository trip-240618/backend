package com.ll.trip.domain.plan.plan.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.ll.trip.domain.plan.plan.entity.Plan;
import com.ll.trip.domain.plan.plan.entity.PlanImage;

import lombok.Data;

@Data
public class PlanCreateResponseDto {
	private Long idx;
	private String title;
	private String content;
	private List<String> imgUris;

	public PlanCreateResponseDto(Plan plan) {
		this.idx = plan.getIdx();
		this.title = plan.getTitle();
		this.content = plan.getContent();
		this.imgUris = plan.getImgUris().stream().map(PlanImage::getUri).collect(Collectors.toList());
	}
}
