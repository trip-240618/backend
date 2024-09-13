package com.ll.trip.domain.trip.planJ.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanJSwapRequestDto {
	@Schema(
		description = "시작일로 부터 몇일 째",
		example = "1")
	private int dayAfterStart;

	@Schema(
		description = "plan pk",
		example = "1")
	private long planId1;

	@Schema(
		description = "plan pk",
		example = "1")
	private long planId2;

}
