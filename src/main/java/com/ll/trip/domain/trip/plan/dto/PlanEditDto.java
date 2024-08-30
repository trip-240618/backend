package com.ll.trip.domain.trip.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanEditDto {
	private long id;
	private long tripId;
	private int dayAfterStart;
	private int OrderByDate;
}
