package com.ll.trip.domain.trip.planP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanPEditRegisterDto {
	private int week;
	private String uuid;
	private String nickname;
}
