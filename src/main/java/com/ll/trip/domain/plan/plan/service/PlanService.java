package com.ll.trip.domain.plan.plan.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ll.trip.domain.plan.plan.dto.PlanCreateRequestDto;
import com.ll.trip.domain.plan.plan.dto.PlanCreateResponseDto;
import com.ll.trip.domain.plan.plan.entity.Plan;
import com.ll.trip.domain.plan.plan.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {
	private final PlanRepository planRepository;

	public synchronized List<PlanCreateResponseDto> getPreviousMessages(Long roomId) {
		return planRepository.findByRoomIdOrderByIndex(roomId);
	}

	public synchronized PlanCreateResponseDto saveMessage(Long roomId, PlanCreateRequestDto requestDto) {

		Plan plan = Plan.builder()
			.roomId(roomId)
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.build();

		plan = planRepository.save(plan);

		return new PlanCreateResponseDto(plan);
	}

	public int swapByIndex(Long roomId, List<Long> orders) {
		return planRepository.swapIndexes(roomId, orders.get(0), orders.get(1));
	}
}
