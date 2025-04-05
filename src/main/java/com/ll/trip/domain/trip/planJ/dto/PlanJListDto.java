package com.ll.trip.domain.trip.planJ.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanJListDto {
	private int dayAfterStart;
	private List<PlanJInfoDto> planList;

	public PlanJListDto(int day){
		this.dayAfterStart = day;
		this.planList = new ArrayList<>();
	}
}
