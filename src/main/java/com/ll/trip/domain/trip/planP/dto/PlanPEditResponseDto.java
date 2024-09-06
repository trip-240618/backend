package com.ll.trip.domain.trip.planP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPEditResponseDto {
	@Schema(
		description = "plan pk",
		example = "1")
	private long planId;

	@Schema(
		description = "이동 후 날짜",
		example = "1")
	private int dayTo;

	@Schema(
		description = "이동 후 순서",
		example = "1")
	private int orderTo;

	@Schema(
		description = "db에서 순서가 바뀐 plan 수",
		example = "5")
	private int updatedCount;
}
