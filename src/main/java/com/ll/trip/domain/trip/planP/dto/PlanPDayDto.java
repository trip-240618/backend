package com.ll.trip.domain.trip.planP.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPDayDto<T> {
	@Schema(example = "1")
	private int day;

	@ArraySchema(schema = @Schema(
		oneOf = {PlanPInfoDto.class}))
	private List<T> planList = new ArrayList<>();

	public PlanPDayDto(int day) {
		this.day = day;
	}
}
