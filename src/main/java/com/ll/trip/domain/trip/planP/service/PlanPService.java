package com.ll.trip.domain.trip.planP.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.planP.dto.PlanPCheckBoxResponseDto;
import com.ll.trip.domain.trip.planP.dto.PlanPCreateRequestDto;
import com.ll.trip.domain.trip.planP.dto.PlanPDeleteDto;
import com.ll.trip.domain.trip.planP.dto.PlanPInfoDto;
import com.ll.trip.domain.trip.planP.entity.PlanP;
import com.ll.trip.domain.trip.planP.repository.PlanPRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanPService {

	private final PlanPRepository planPRepository;

	public PlanP createPlanP(Trip trip, PlanPCreateRequestDto requestDto, String uuid) {

		PlanP plan = PlanP.builder()
			.trip(trip)
			.dayAfterStart(requestDto.getDayAfterStart())
			.content(requestDto.getContent())
			.orderByDate(
				getNextIdx(trip.getId(), requestDto.getDayAfterStart())
			)
			.writerUuid(uuid)
			.checkbox(false)
			.build();

		return planPRepository.save(plan);
	}

	public int getNextIdx(long tripId, int dayAfterStart) {
		Integer maxOrder = planPRepository.findMaxOrder(tripId, dayAfterStart);
		return maxOrder == null ? 0 : maxOrder + 1;
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

	public List<PlanPInfoDto> findAllByTripId(Long tripId) {
		return planPRepository.findAllByTripIdOrderByDayAfterStartAndOrderByDate(tripId);
	}

	@Transactional
	public PlanP updatePlanPByPlanId(PlanPInfoDto requestBody) {
		PlanP plan = planPRepository.findPlanPById(requestBody.getPlanId()).orElseThrow(NullPointerException::new);

		plan.setContent(requestBody.getContent());
		plan.setCheckbox(requestBody.isCheckbox());

		return planPRepository.save(plan);
	}

	@Transactional
	public int deletePlanPByPlanId(Long planId) {
		Optional<PlanPDeleteDto> optDto = planPRepository.findPlanPDeleteDtoByPlanId(planId);
		PlanPDeleteDto dto = optDto.orElseThrow(NullPointerException::new);

		int updated = reduceOrderBiggerThanPlanOrder(dto.getTripId(), dto.getDayAfterStart(),
			dto.getOrderByDate());

		planPRepository.deleteById(planId);

		return updated;
	}

	@Transactional
	public int reduceOrderBiggerThanPlanOrder(long tripId, int dayAfterStart, int orderByDate) {
		return planPRepository.reduceOrderBiggerThanOrder(tripId, dayAfterStart, orderByDate);
	}

	public PlanPCheckBoxResponseDto updateCheckBoxById(Long planId) {
		boolean checkbox = planPRepository.findIsCheckBoxByPlanId(planId);
		int cnt = planPRepository.updateCheckBoxByPlanId(planId, !checkbox);

		if (cnt == 0)
			return null;

		return new PlanPCheckBoxResponseDto(planId, !checkbox);
	}
}
