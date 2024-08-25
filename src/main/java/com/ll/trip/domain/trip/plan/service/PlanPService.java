package com.ll.trip.domain.trip.plan.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ll.trip.domain.trip.plan.dto.PlanPCreateRequestDto;
import com.ll.trip.domain.trip.plan.dto.PlanPInfoDto;
import com.ll.trip.domain.trip.plan.entity.PlanP;
import com.ll.trip.domain.trip.plan.repository.PlanPRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
		Integer maxIdx = planPRepository.findMaxIdx(tripId, dayAfterStart);
		return maxIdx == null ? 0 : maxIdx + 1;
	}

	public PlanPInfoDto convertPlanPToDto(PlanP plan) {
		return new PlanPInfoDto(
			plan.getDayAfterStart(),
			plan.getOrderByDate(),
			plan.getWriterUuid(),
			plan.getContent(),
			plan.isCheckbox()
		);
	}

	public List<PlanP> findAllByTripId(Long tripId) {
		return planPRepository.findAllByTripIdOrderByDayAfterStartAndOrderByDate(tripId);
	}
}
