package com.ll.trip.domain.trip.planP.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PlanPListDto {
	private int dayAfterStart;
	private List<PlanPInfoDto> planList;

	public PlanPListDto(int day){
		this.dayAfterStart = day;
		planList = new ArrayList<>();
	}
}
