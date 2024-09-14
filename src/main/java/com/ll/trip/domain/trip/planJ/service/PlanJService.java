package com.ll.trip.domain.trip.planJ.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.trip.domain.flight.dto.ScheduleResponseDto;
import com.ll.trip.domain.trip.location.dto.LocationDto;
import com.ll.trip.domain.trip.location.service.LocationService;
import com.ll.trip.domain.trip.planJ.dto.PlanJCreateRequestDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJModifyRequestDto;
import com.ll.trip.domain.trip.planJ.entity.PlanJ;
import com.ll.trip.domain.trip.planJ.repository.PlanJRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanJService {

	private final PlanJRepository planJRepository;
	private final LocationService locationService;
	private final PlanJEditService planJEditService;

	@Transactional
	public void deletePlanJById(Long planId) {
		planJRepository.deleteById(planId);
	}

	@Transactional
	public PlanJ createPlan(Trip trip, PlanJCreateRequestDto requestDto, int order, String uuid) {

		PlanJ plan = PlanJ.builder()
			.trip(trip)
			.dayAfterStart(requestDto.getDayAfterStart())
			.orderByDate(order)
			.writerUuid(uuid)
			.latitude(requestDto.getLatitude())
			.longitude(requestDto.getLongitude())
			.memo(requestDto.getMemo())
			.title(requestDto.getTitle())
			.build();

		return planJRepository.save(plan);
	}

	public PlanJInfoDto convertPlanJToDto(PlanJ plan) {
		return new PlanJInfoDto(plan);
	}

	public Map<String, PlanJ> createPlanJFromScheduledResponseDto(Trip trip, ScheduleResponseDto dto,
		String uuid) {

		String airline = dto.getAirlineCode() + dto.getAirlineNumber();

		String memo;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			memo = objectMapper.writeValueAsString(dto);
		} catch (JsonProcessingException e) {
			throw new UnexpectedRollbackException("ScheduleResponseDto의 jsonString 변환 실패");
		}

		if (planJRepository.updateFlightCntByTripId(trip.getId()) == 0) {
			throw new UnexpectedRollbackException("trip flightCnt 업데이트 실패");
		}

		int updatedFlightCnt = trip.getFlightCnt() + 1;

		PlanJ departPlan = createAirlinePlanJ(trip, dto.getDepartureDate(), getLocalTime(dto.getDepartureDate()),
			updatedFlightCnt, uuid,
			airline, dto.getDepartureAirport_kr(), dto.getDepartureAirport(), " 출발",
			memo);

		PlanJ arrivalPlan = createAirlinePlanJ(trip, dto.getArrivalDate(), getLocalTime(dto.getArrivalDate()),
			updatedFlightCnt, uuid,
			airline, dto.getArrivalAirport_kr(), dto.getArrivalAirport(), " 도착",
			memo);

		planJRepository.saveAll(List.of(departPlan, arrivalPlan));

		return Map.of("departure", departPlan, "arrival", arrivalPlan);
	}

	private PlanJ createAirlinePlanJ(Trip trip, String date, LocalTime startTime, int flightId, String uuid,
		String airline, String airportKr, String airportIata, String prefix, String memo) {

		int daysDifference = getDaysDifference(trip.getStartDate(), date);
		int order = planJEditService.getLastOrderByTripId(trip.getId(), daysDifference);

		LocationDto location = locationService.getPlaceLocation(airportKr + airportIata);

		return PlanJ.builder()
			.trip(trip)
			.dayAfterStart(daysDifference)
			.startTime(startTime)
			.flightId(flightId)
			.writerUuid(uuid)
			.title(airline + " " + airportKr + prefix)
			.memo(memo)
			.longitude(location.getLongitude())
			.latitude(location.getLatitude())
			.orderByDate(order)
			.build();
	}

	public int getDaysDifference(LocalDate startDate, String date) {
		OffsetDateTime offsetDateTime = OffsetDateTime.parse(date);
		LocalDate dateFromOffsetDateTime = offsetDateTime.toLocalDate();

		return (int)ChronoUnit.DAYS.between(startDate, dateFromOffsetDateTime) + 1;
	}

	public LocalTime getLocalTime(String date) {
		OffsetDateTime offsetDateTime = OffsetDateTime.parse(date);
		return offsetDateTime.toLocalTime();
	}

	public List<PlanJInfoDto> findAllByTripIdAndDay(long tripId, int day) {
		return planJRepository.findAllByTripIdAndDay(tripId, day);
	}

	@Transactional
	public PlanJ updatePlanJByPlanId(PlanJ plan, PlanJModifyRequestDto requestBody, int order) {
		plan.setTitle(requestBody.getTitle());
		plan.setMemo(requestBody.getMemo());
		plan.setDayAfterStart(requestBody.getDayAfterStart());
		plan.setStartTime(requestBody.getStartTime());
		plan.setLatitude(requestBody.getLatitude());
		plan.setLongitude(requestBody.getLongitude());
		plan.setOrderByDate(order);

		return planJRepository.save(plan);
	}

	public PlanJ findPlanJById(long planId) {
		return planJRepository.findById(planId).orElseThrow(NullPointerException::new);
	}

}
