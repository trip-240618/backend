package com.ll.trip.domain.trip.location.dto;

import lombok.Data;

@Data
public class TextQueryLocationResponseDto {
	private Place[] places;

	@Data
	public static class Place {
		private LocationDto location;
	}
}
