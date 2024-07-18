package com.ll.trip.domain.plan.plan.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.plan.plan.dto.PlanCreateRequestDto;
import com.ll.trip.domain.plan.plan.dto.PlanCreateResponseDto;
import com.ll.trip.domain.plan.plan.entity.Plan;
import com.ll.trip.domain.plan.plan.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {
	private final PlanRepository planRepository;

	private final ConcurrentMap<Long, String> swapUsers = new ConcurrentHashMap<>();

	public synchronized List<PlanCreateResponseDto> getPreviousMessages(Long roomId) {
		return planRepository.findByRoomIdOrderByIndex(roomId);
	}

	@Transactional
	public synchronized PlanCreateResponseDto savePlan(Long roomId, PlanCreateRequestDto requestDto) {
		long idx = getNextIdx();

		Plan plan = Plan.builder()
			.roomId(roomId)
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.idx(idx)
			.build();

		plan = planRepository.save(plan);

		return new PlanCreateResponseDto(plan);
	}

	public int swapByIndex(Long roomId, List<Long> orders) {
		if (!swapUsers.containsKey(roomId))
			return 0;

		int swapped = planRepository.swapIndexes(roomId, orders.get(0), orders.get(1));
		swapUsers.remove(roomId);
		return swapped;
	}

	public Long getNextIdx() {
		Long maxIdx = planRepository.findMaxIdx();
		return (maxIdx != null) ? maxIdx + 1 : 0;
	}

	public boolean addSwapUserIfPossible(Long roomId) {
		String user = swapUsers.get(roomId);

		if (user != null)
			return false;

		swapUsers.put(roomId, "User");
		return true;
	}

	public void deleteSwapUser(Long roomId) {
		swapUsers.remove(roomId);
	}

	public Map<Long, String> showSwapUser(){
		return swapUsers;
	}
}
