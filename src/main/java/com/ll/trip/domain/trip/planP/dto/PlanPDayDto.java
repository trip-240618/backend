package com.ll.trip.domain.trip.planP.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PlanPDayDto<T> {
	@Schema(example = "1")
	private Integer day;

	@ArraySchema(schema = @Schema(
		oneOf = {PlanPInfoDto.class}))
	private List<T> planList = new ArrayList<>();

	public PlanPDayDto(Integer day) {
		this.day = day;
	}
}
