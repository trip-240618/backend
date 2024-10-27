package com.ll.trip.domain.trip.planJ.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PlanJOrderDto {
	@Schema(
		description = "plan pk",
		example = "1")
	private Long planId;

	@Schema(
		description = "일정 시작 시간",
		example = "14:30")
	private LocalTime startTime;

	@Schema(
		description = "예정일 별 순서",
		example = "2")
	private Integer orderByDate;
}
