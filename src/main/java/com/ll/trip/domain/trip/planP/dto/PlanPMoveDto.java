package com.ll.trip.domain.trip.planP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPMoveDto {
	@Schema(
		description = "plan pk",
		example = "1")
	private long planId;
	@Schema(
		description = "이동 전 day",
		example = "1")
	private int dayFrom;
	@Schema(
		description = "이동 후 day",
		example = "2")
	private int dayTo;
	@Schema(
		description = "이동 전 order",
		example = "1")
	private int orderFrom;
	@Schema(
		description = "이동 후 order",
		example = "1")
	private int orderTo;
}
