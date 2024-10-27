package com.ll.trip.domain.flight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.flight.dto.ScheduleResponseDto;
import com.ll.trip.domain.flight.entity.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {
	@Query("""
		select new com.ll.trip.domain.flight.dto.ScheduleResponseDto(
			f.id,
			f.airlineCode,
			f.airlineNumber,
			f.departureDate,
			f.departureAirport,
			f.departureAirport_kr,
			f.arrivalDate,
			f.arrivalAirport,
			f.arrivalAirport_kr
		) from Flight f
		where f.trip.id =:tripId
		""")
	List<ScheduleResponseDto> findByTrip_Id(long tripId);
}
