package com.ll.trip.domain.trip.planP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPCheckBoxResponseDto {
	@Schema(
		description = "plan pk",
		example = "1")
	private long planId;

	@Schema(
		description = "예정일",
		example = "1")
	private int dayAfterStart;

	@Schema(
		description = "변경 후 check 여부",
		example = "true")
	private boolean checkbox;
}
