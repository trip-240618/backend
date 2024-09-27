package com.ll.trip.domain.trip.planP.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.planP.dto.PlanPCheckBoxResponseDto;
import com.ll.trip.domain.trip.planP.dto.PlanPCreateRequestDto;
import com.ll.trip.domain.trip.planP.dto.PlanPInfoDto;
import com.ll.trip.domain.trip.planP.dto.PlanPLockerDto;
import com.ll.trip.domain.trip.planP.entity.PlanP;
import com.ll.trip.domain.trip.planP.repository.PlanPRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanPService {

	private final PlanPRepository planPRepository;
	private final EntityManager entityManager;

	@Transactional
	public PlanP createPlanP(long tripId, PlanPCreateRequestDto requestDto, String uuid) {
		Trip trip = entityManager.getReference(Trip.class, tripId);

		PlanP plan = PlanP.builder()
			.trip(trip)
			.dayAfterStart(requestDto.getDayAfterStart())
			.content(requestDto.getContent())
			.orderByDate(
				getNextIdx(trip.getId(), requestDto.getDayAfterStart())
			)
			.writerUuid(uuid)
			.checkbox(false)
			.locker(requestDto.isLocker())
			.build();

		return planPRepository.save(plan);
	}

	public int getNextIdx(long tripId, Integer day) {
		Integer maxOrder = planPRepository.findMaxOrder(tripId, day);
		return maxOrder == null ? 0 : maxOrder + 11;
	}

	public PlanPInfoDto convertPlanPToDto(PlanP plan) {
		return new PlanPInfoDto(
			plan.getId(),
			plan.getDayAfterStart(),
			plan.getOrderByDate(),
			plan.getWriterUuid(),
			plan.getContent(),
			plan.isCheckbox()
		);
	}

	public List<PlanPInfoDto> findAllByTripId(Long tripId, boolean locker) {
		return planPRepository.findAllByTripIdOrderByDayAfterStartAndOrderByDate(tripId, locker);
	}

	@Transactional
	public PlanP updatePlanPByPlanId(PlanPInfoDto requestBody) {
		PlanP plan = planPRepository.findPlanPById(requestBody.getPlanId()).orElseThrow(NullPointerException::new);

		plan.setContent(requestBody.getContent());
		plan.setCheckbox(requestBody.isCheckbox());

		return planPRepository.save(plan);
	}

	@Transactional
	public void deletePlanPByPlanId(Long planId) {
		planPRepository.deleteById(planId);
	}

	@Transactional
	public PlanPCheckBoxResponseDto updateCheckBoxById(Long planId) {
		boolean checkbox = planPRepository.findIsCheckBoxByPlanId(planId);
		int cnt = planPRepository.updateCheckBoxByPlanId(planId, !checkbox);

		if (cnt == 0)
			return null;

		return new PlanPCheckBoxResponseDto(planId, !checkbox);
	}

	@Transactional
	public PlanPLockerDto moveLocker(long tripId, long planId, Integer dayTo, boolean locker) {
		int order = getNextIdx(tripId, dayTo);
		if (planPRepository.updatePlanPDayAndLockerByPlanId(planId, dayTo, order, locker) > 0)
			return new PlanPLockerDto(planId, dayTo, order, locker);
		else
			return null;
	}
}
