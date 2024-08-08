package com.ll.trip.domain.flight.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.DatedFlight;
import com.ll.trip.domain.flight.dto.ScheduleResponseDto;
import com.ll.trip.domain.flight.service.FlightService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/flight")
public class FlightController {

	private final FlightService flightService;

	@GetMapping("/schedule")
	public ResponseEntity<?> showFlightSchedule(
		@RequestParam Integer flightNumber,
		@RequestParam String carrierCode,
		@RequestParam String departureDate
	) {
		log.info(carrierCode + flightNumber + " " + departureDate);

		DatedFlight[] flightStatus = null;
		try {
			flightStatus = flightService.getFlightInfo(flightNumber, carrierCode, departureDate);
		} catch (ResponseException re) {
			log.error(re.getMessage());
		}

		if (flightStatus == null || flightStatus.length == 0)
			return ResponseEntity.internalServerError().body("response is null");

		log.info("statusCode: " + flightStatus[0].getResponse().getStatusCode());

		if (flightStatus[0].getResponse().getStatusCode() != 200)
			return ResponseEntity.internalServerError()
				.body("Wrong status code: " + flightStatus[0].getResponse().getStatusCode());

		ScheduleResponseDto responseDto = flightService.parseToDto(flightStatus);

		return ResponseEntity.ok(responseDto);
	}

}
