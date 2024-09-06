package com.ll.trip.domain.trip.planP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPEditDto {
	private long id;
	private long tripId;
	private int dayAfterStart;
	private int OrderByDate;
}
