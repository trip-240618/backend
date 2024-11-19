package com.ll.trip.domain.trip.planP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPEditRegisterDto {
	private int week;
	private String uuid;
	private String nickname;
}
