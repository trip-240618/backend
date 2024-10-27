package com.ll.trip.domain.trip.planJ.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanJSwapRequestDto {
	@Schema(
		description = "시작일 기준 몇일째인지",
		example = "1")
	private int dayAfterStart;

	private List<PlanJOrderDto> orderDtos = new ArrayList<>();
}
