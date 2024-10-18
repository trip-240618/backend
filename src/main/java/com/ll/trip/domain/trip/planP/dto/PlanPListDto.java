package com.ll.trip.domain.trip.planP.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PlanPListDto {
	private Integer dayAfterStart;
	private List<PlanPInfoDto> planList;

	public PlanPListDto(Integer day){
		this.dayAfterStart = day;
		planList = new ArrayList<>();
	}
}
