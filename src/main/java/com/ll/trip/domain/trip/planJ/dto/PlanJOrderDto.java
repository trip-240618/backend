package com.ll.trip.domain.trip.planJ.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanJOrderDto {
	private Integer maxOrder;
	private Integer firstBiggerOrder;
}
