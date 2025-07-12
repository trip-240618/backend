package com.ll.trip.domain.trip.planP.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPWeekDto<T> {
	@Schema(example = "1")
	private int week;

	private List<PlanPDayDto<T>> dayList = new ArrayList<>();

	public PlanPWeekDto(int week) {
		this.week = week;
	}
}
