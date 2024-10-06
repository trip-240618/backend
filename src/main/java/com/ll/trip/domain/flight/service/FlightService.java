package com.ll.trip.domain.flight.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.DatedFlight;
import com.ll.trip.domain.flight.dto.ScheduleResponseDto;
import com.ll.trip.domain.flight.entity.Airport;
import com.ll.trip.domain.flight.entity.Flight;
import com.ll.trip.domain.flight.repository.AirportRepository;
import com.ll.trip.domain.flight.repository.FlightRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlightService {

	private final Amadeus amadeus;
	private final AirportRepository airportRepository;
	private final FlightRepository flightRepository;
	private final EntityManager entityManager;

	public DatedFlight[] getFlightInfo(
		int flightNumber,
		String carrierCode,
		String scheduledDepartureDate)
		throws ResponseException {
		DatedFlight[] flightStatus = amadeus.schedule.flights.get(Params
			.with("flightNumber", flightNumber)
			.and("carrierCode", carrierCode)
			.and("scheduledDepartureDate", scheduledDepartureDate)
		);

		return flightStatus;
	}

	public ScheduleResponseDto parseToDto(String airlineCode, int airlineNumber, DatedFlight[] flightStatus) {
		int last = flightStatus[0].getFlightPoints().length - 1;

		DatedFlight.FlightPoint flightPoint_d = flightStatus[0].getFlightPoints()[0];

		String departureDate = flightPoint_d.getDeparture().getTimings()[0].getValue(); //STD
		String departureAirport = flightPoint_d.getIataCode();
		String departureAirport_kr;

		DatedFlight.FlightPoint flightPoint_a = flightStatus[0].getFlightPoints()[last];
		String arrivalDate = flightPoint_a.getArrival().getTimings()[0].getValue(); //STA
		String arrivalAirport = flightPoint_a.getIataCode();
		String arrivalAirport_kr;

		List<Airport> airports = airportRepository.findByIata(departureAirport, arrivalAirport);

		if (airports.size() == 2) {
			departureAirport_kr = airports.get(0).getKorName();
			arrivalAirport_kr = airports.get(1).getKorName();
		} else {
			departureAirport_kr = null;
			arrivalAirport_kr = null;
		}

		return ScheduleResponseDto.builder()
			.airlineCode(airlineCode)
			.airlineNumber(airlineNumber)
			.departureDate(departureDate)
			.departureAirport(departureAirport)
			.departureAirport_kr(departureAirport_kr)
			.arrivalDate(arrivalDate)
			.arrivalAirport(arrivalAirport)
			.arrivalAirport_kr(arrivalAirport_kr)
			.build();
	}

	@Transactional
	public ScheduleResponseDto createFlight(ScheduleResponseDto dto, long tripId) {
		Trip tripRef = entityManager.getReference(Trip.class, tripId);

		Flight flight = flightRepository.save(
				Flight.builder()
				.airlineCode(dto.getAirlineCode())
				.arrivalAirport(dto.getArrivalAirport())
				.arrivalAirport_kr(dto.getArrivalAirport_kr())
				.airlineNumber(dto.getAirlineNumber())
				.arrivalDate(dto.getArrivalDate())
				.departureAirport(dto.getDepartureAirport())
				.departureAirport_kr(dto.getDepartureAirport_kr())
				.departureDate(dto.getDepartureDate())
				.trip(tripRef)
				.build());

		dto.setFlightId(flight.getId());
		return dto;
	}

	public List<ScheduleResponseDto> findByTripId(long tripId) {
		return flightRepository.findByTrip_Id(tripId);
	}

	public void deleteFlight(long flightId) {
		flightRepository.deleteById(flightId);
	}
}
