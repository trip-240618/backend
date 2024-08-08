package com.ll.trip.domain.flight.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseDto {
	private String departureDate; //STD
	private String departureAirport;
	private String departureAirport_kr;

	private String arrivalDate; //STA
	private String arrivalAirport;
	private String arrivalAirport_kr;
}
