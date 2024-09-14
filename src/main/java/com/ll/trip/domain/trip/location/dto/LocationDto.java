package com.ll.trip.domain.trip.location.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationDto {
	private BigDecimal latitude;
	private BigDecimal longitude;
}
