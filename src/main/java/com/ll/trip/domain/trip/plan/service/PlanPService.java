package com.ll.trip.domain.trip.plan.service;

import java.time.LocalDate;

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
			.startDate(requestDto.getStartDate())
			.content(requestDto.getContent())
			.orderByDate(
				getNextIdx(trip.getId(), requestDto.getStartDate())
			)
			.writerUuid(uuid)
			.checkbox(false)
			.build();

		return planPRepository.save(plan);
	}

	public int getNextIdx(long tripId, LocalDate startDate) {
		Integer maxIdx = planPRepository.findMaxIdx(tripId, startDate);
		return maxIdx == null ? 0 : maxIdx + 1;
	}

	public PlanPInfoDto convertPlanPToDto(PlanP plan) {
		return new PlanPInfoDto(
			plan.getStartDate(),
			plan.getOrderByDate(),
			plan.getWriterUuid(),
			plan.getContent(),
			plan.isCheckbox()
		);
	}
}
