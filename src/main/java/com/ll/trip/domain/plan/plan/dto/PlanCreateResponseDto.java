package com.ll.trip.domain.plan.plan.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.ll.trip.domain.plan.plan.entity.Plan;
import com.ll.trip.domain.plan.plan.entity.PlanImage;

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
	private List<String> imgUris;

	public PlanCreateResponseDto(Long idx, PlanCreateRequestDto requestDto) {
		this.idx = idx;
		this.title = requestDto.getTitle();
		this.content = requestDto.getContent();
		this.imgUris = requestDto.getImgUrls();
	}

	public PlanCreateResponseDto(Plan plan) {
		this.idx = plan.getIdx();
		this.title = plan.getTitle();
		this.content = plan.getContent();
		this.imgUris = plan.getImgUris().stream().map(PlanImage::getUri).collect(Collectors.toList());
	}
}
