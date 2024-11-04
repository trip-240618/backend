package com.ll.trip.domain.trip.planP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPOrderDto {
	private long id;
	private int dayAfterStart;
	private int orderByDate;
}
