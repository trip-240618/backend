package com.ll.trip.domain.flight.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.DatedFlight;
import com.ll.trip.domain.flight.dto.ScheduleResponseDto;
import com.ll.trip.domain.flight.service.FlightService;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.service.TripService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/flight")
public class FlightController {

	private final FlightService flightService;
	private final TripService tripService;

	@PostMapping("/{invitationCode}/flight/create")
	@Operation(summary = "항공편 조회")
	@ApiResponse(responseCode = "200", description = "항공편으로 항공기 출발,도착 정보 조회", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScheduleResponseDto.class))})
	public ResponseEntity<?> createFlightSchedule(
		@PathVariable String invitationCode,
		@Parameter(description = "항공편 번호", example = "319") @RequestParam Integer flightNumber,
		@Parameter(description = "항공사 코드", example = "AZ") @RequestParam String carrierCode,
		@Parameter(description = "출발 날짜(과거x)", example = "2024-08-15") @RequestParam String departureDate
	) {
		log.info(carrierCode + flightNumber + " " + departureDate);

		Trip trip = tripService.findByInvitationCode(invitationCode);

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

		ScheduleResponseDto responseDto = flightService.createFlight(flightStatus, trip);

		return ResponseEntity.ok(responseDto);
	}

	@PostMapping("/{invitationCode}/flight/list")
	@Operation(summary = "항공편 조회")
	@ApiResponse(responseCode = "200", description = "항공편으로 항공기 출발,도착 정보 조회", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScheduleResponseDto.class))})
	public ResponseEntity<?> showFlightSchedule(
		@PathVariable String invitationCode
	) {
		List<ScheduleResponseDto> responseDto = flightService.findByInvitationCode(invitationCode);

		return ResponseEntity.ok(responseDto);
	}

}
