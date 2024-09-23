package com.ll.trip.domain.trip.planJ.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
