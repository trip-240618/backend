package com.ll.trip.domain.plan.plan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.ll.trip.domain.plan.plan.entity.Plan;
import com.ll.trip.domain.plan.plan.repository.PlanRepository;
import com.ll.trip.domain.plan.plan.dto.PlanDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {
	private final PlanRepository planRepository;

	private static final int MEMORY_MESSAGE_LIMIT = 10;
	private final Map<Long, List<PlanDto>> recentPlans = new ConcurrentHashMap<>();

	public synchronized List<PlanDto> getPreviousMessages(Long roomId) {
		// DB에서 Plan 객체 리스트를 가져옴
		List<PlanDto> plans = planRepository.findByRoomIdOrderByIndex(roomId);

		if (plans.size() == 0)
			return plans;

		long lastOrder = plans.get(plans.size() - 1).getIndex();
		List<PlanDto> recentPlanList = recentPlans.get(roomId);

		if (recentPlanList == null)
			return plans;

		for (int i = recentPlanList.size() - 1; i >= 0; i--) {
			PlanDto recent = recentPlanList.get(i);
			if (recent.getIndex() > lastOrder) {
				plans.add(recent);
			}
		}

		return plans;
	}

	public synchronized void saveMessage(Long roomId, PlanDto requestDto) {
		// 메모리에 메시지 추가
		recentPlans.putIfAbsent(roomId, new ArrayList<>());
		// 주기적으로 메모리에서 DB로 메시지 저장
		List<PlanDto> messages = recentPlans.get(roomId);
		messages.add(requestDto);

		Plan plan = Plan.builder()
			.roomId(roomId)
			.idx(requestDto.getIndex())
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.build();

		planRepository.save(plan);

		if (messages.size() > MEMORY_MESSAGE_LIMIT) {
			//TODO 최신 메세지 몇개 남기기
			messages.clear();
		}
	}

	public void swapByIndex(Long roomId, List<Long> orders) {
		planRepository.swapIndexes(roomId, orders.get(0), orders.get(1));
	}
}
