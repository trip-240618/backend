package com.ll.trip.domain.trip.planJ.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.planJ.dto.PlanJCreateRequestDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJListDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJModifyRequestDto;
import com.ll.trip.domain.trip.planJ.entity.PlanJ;
import com.ll.trip.domain.trip.planJ.repository.PlanJRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanJService {

	private final PlanJRepository planJRepository;
	private final EntityManager entityManager;

	@Transactional
	public void deletePlanJById(Long planId) {
		planJRepository.deleteById(planId);
	}

	@Transactional
	public PlanJ createPlan(long tripId, PlanJCreateRequestDto requestDto, int order, String uuid) {
		Trip trip = entityManager.getReference(Trip.class, tripId);
		LocalTime startTime = requestDto.getStartTime();

		PlanJ plan = PlanJ.builder()
			.trip(trip)
			.dayAfterStart(requestDto.getDayAfterStart())
			.orderByDate(order)
			.writerUuid(uuid)
			.startTime(startTime == null ? LocalTime.now() : startTime)
			.latitude(requestDto.getLatitude())
			.longitude(requestDto.getLongitude())
			.place(requestDto.getPlace())
			.memo(requestDto.getMemo())
			.title(requestDto.getTitle())
			.locker(requestDto.isLocker())
			.build();

		return planJRepository.save(plan);
	}

	public PlanJInfoDto convertPlanJToDto(PlanJ plan) {
		return new PlanJInfoDto(plan);
	}

	public List<PlanJListDto> findAllPlanAByTripIdAndDay(long tripId, int day) {
		List<PlanJInfoDto> dtos = planJRepository.findAllPlanAByTripIdAndDay(tripId, day, false);
		return parseToListResponse(dtos);
	}

	public List<PlanJListDto> findAllPlanBByTripId(long tripId) {
		List<PlanJInfoDto> dtos = planJRepository.findAllPlanBByTripIdAndDay(tripId, true);
		return parseToListResponse(dtos);
	}

	private List<PlanJListDto> parseToListResponse(List<PlanJInfoDto> dtos) {
		Map<Integer, List<PlanJInfoDto>> dayMap = new HashMap<>();
		List<PlanJListDto> response = new ArrayList<>();

		for (PlanJInfoDto dto : dtos) {
			dayMap.computeIfAbsent(dto.getDayAfterStart(), day -> {
				PlanJListDto listDto = new PlanJListDto(day);
				response.add(listDto);
				return listDto.getPlanList();
			}).add(dto);
		}

		return response;
	}

	@Transactional
	public PlanJ updatePlanJByPlanId(PlanJ plan, PlanJModifyRequestDto requestBody, int order) {
		plan.setTitle(requestBody.getTitle());
		plan.setMemo(requestBody.getMemo());
		plan.setDayAfterStart(requestBody.getDayAfterStart());
		plan.setStartTime(requestBody.getStartTime());
		plan.setLatitude(requestBody.getLatitude());
		plan.setLongitude(requestBody.getLongitude());
		plan.setPlace(requestBody.getPlace());
		plan.setOrderByDate(order);
		plan.setLocker(requestBody.isLocker());

		return planJRepository.save(plan);
	}

	public PlanJ findPlanJById(long planId) {
		return planJRepository.findById(planId).orElseThrow(NullPointerException::new);
	}

	@Transactional
	public void updatePlanJDay(Long tripId, int dayDiffer, int duration) {
		planJRepository.updateDayByTripId(tripId, dayDiffer);
		planJRepository.deleteByTripIdAndDuration(tripId, duration);
	}
}
