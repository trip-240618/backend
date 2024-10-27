package com.ll.trip.domain.flight.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.DatedFlight;
import com.ll.trip.domain.flight.dto.ScheduleResponseDto;
import com.ll.trip.domain.flight.service.FlightService;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/flight")
@Tag(name = "Flight", description = "항공편 API")
public class FlightController {

	private final FlightService flightService;
	private final TripService tripService;

	@GetMapping("/search")
	@Operation(summary = "항공편 조회")
	@ApiResponse(responseCode = "200", description = "항공편으로 항공기 출발,도착 정보 조회", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScheduleResponseDto.class))})
	public ResponseEntity<?> searchFlightSchedule(
		@Parameter(description = "항공편 번호", example = "319") @RequestParam Integer flightNumber,
		@Parameter(description = "항공사 코드", example = "AZ") @RequestParam String carrierCode,
		@Parameter(description = "출발 날짜(과거x)", example = "2024-08-15") @RequestParam String departureDate
	) {
		log.info(carrierCode + flightNumber + " " + departureDate);

		DatedFlight[] flightStatus = null;
		try {
			flightStatus = flightService.getFlightInfo(flightNumber, carrierCode, departureDate);
		} catch (ResponseException re) {
			log.error(re.getMessage());
		}

		if (flightStatus == null || flightStatus.length == 0)
			return ResponseEntity.ok(null);

		log.info("statusCode: " + flightStatus[0].getResponse().getStatusCode());

		if (flightStatus[0].getResponse().getStatusCode() != 200)
			throw new NoSuchElementException("wrong status code");

		ScheduleResponseDto responseDto = flightService.parseToDto(carrierCode, flightNumber, flightStatus);

		return ResponseEntity.ok(responseDto);
	}

	@PostMapping("/trip/{tripId}/create")
	@Operation(summary = "항공편 저장")
	@ApiResponse(responseCode = "200", description = "조회된 항공편을 저장", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScheduleResponseDto.class))})
	public ResponseEntity<?> createFlightSchedule(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestBody ScheduleResponseDto dto
	) {
		ScheduleResponseDto responseDto = flightService.createFlight(dto, tripId);

		return ResponseEntity.ok(responseDto);
	}

	@DeleteMapping("/trip/{tripId}/delete")
	@Operation(summary = "항공편 삭제")
	@ApiResponse(responseCode = "200", description = "항공편 삭제", content = {
		@Content(mediaType = "application/json", examples = @ExampleObject(name = "삭제 성공", value = "deleted"))})
	public ResponseEntity<?> createFlightSchedule(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestParam @Parameter(description = "삭제할 Flight id", example = "1") long flightId
	) {
		tripService.checkTripMemberByTripIdAndUserId(tripId, securityUser.getId());
		flightService.deleteFlight(flightId);

		return ResponseEntity.ok("deleted");
	}

	@GetMapping("/trip/{tripId}/list")
	@Operation(summary = "trip 항공편 목록 조회")
	@ApiResponse(responseCode = "200", description = "항공편으로 항공기 출발,도착 정보 조회", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScheduleResponseDto.class))})
	public ResponseEntity<?> showFlightSchedule(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId
	) {
		List<ScheduleResponseDto> responseDto = flightService.findByTripId(tripId);

		return ResponseEntity.ok(responseDto);
	}

}
