package com.ll.trip.domain.trip.planJ.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanJDeleteDto {
	private Long tripId;
	private Long planId;
	private Integer dayAfterStart;
	private Integer orderByDate;
}
