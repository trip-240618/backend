package com.ll.trip.domain.trip.planJ.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.planJ.dto.PlanJCreateRequestDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto;
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
			.memo(requestDto.getMemo())
			.title(requestDto.getTitle())
			.locker(requestDto.isLocker())
			.build();

		return planJRepository.save(plan);
	}

	public PlanJInfoDto convertPlanJToDto(PlanJ plan) {
		return new PlanJInfoDto(plan);
	}

	public List<PlanJInfoDto> findAllPlanAByTripIdAndDay(long tripId, int day) {
		return planJRepository.findAllPlanAByTripIdAndDay(tripId, day, false);
	}

	public List<PlanJInfoDto> findAllPlanBByTripId(long tripId) {
		return planJRepository.findAllPlanBByTripIdAndDay(tripId, true);
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
