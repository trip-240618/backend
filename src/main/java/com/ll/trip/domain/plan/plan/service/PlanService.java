package com.ll.trip.domain.plan.plan.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.file.file.dto.UploadImageRequestBody;
import com.ll.trip.domain.plan.plan.dto.PlanCreateRequestDto;
import com.ll.trip.domain.plan.plan.dto.PlanCreateResponseDto;
import com.ll.trip.domain.plan.plan.dto.PlanDeleteRequestDto;
import com.ll.trip.domain.plan.plan.entity.Plan;
import com.ll.trip.domain.plan.plan.entity.PlanImage;
import com.ll.trip.domain.plan.plan.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {
	private final PlanRepository planRepository;

	private final ConcurrentMap<Long, String> swapUsers = new ConcurrentHashMap<>();

	public synchronized List<PlanCreateResponseDto> getPreviousMessages(Long roomId) {
		//fetch join
		List<Plan> plans = planRepository.findByRoomIdOrderByIndex(roomId);

		List<PlanCreateResponseDto> responseDtos = new ArrayList<>();
		for (Plan plan : plans) {
			PlanCreateResponseDto responseDto = new PlanCreateResponseDto(plan);
			responseDtos.add(responseDto);
		}

		return responseDtos;
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

		savePlanImg(plan, requestDto.getImgUrls());

		return new PlanCreateResponseDto(plan);
	}

	public Long getNextIdx() {
		Long maxIdx = planRepository.findMaxIdx();
		return maxIdx == null ? 0 : maxIdx + 1;
	}

	public int swapByIndex(Long roomId, List<Long> orders) {
		if (!swapUsers.containsKey(roomId))
			return 0;

		int swapped = planRepository.swapIndexes(roomId, orders.get(0), orders.get(1));
		swapUsers.remove(roomId);
		return swapped;
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

	public Map<Long, String> showSwapUser() {
		return swapUsers;
	}

	@Transactional
	public long deletePlan(Long roomId, PlanDeleteRequestDto requestDto) {
		Long idx = requestDto.getIdx();
		return planRepository.deleteByIdx(idx) == 1 ? idx : -1;
	}

	@Transactional
	public void savePlanImg(Plan plan, List<String> urls) {
		List<PlanImage> imgUrls = (urls == null ?
			Collections.<String>emptyList() : urls)
			.stream()
			.map(url -> PlanImage.builder()
				.uri(url)
				.build()
			)
			.toList();

		for (PlanImage img : imgUrls) {
			plan.addImg(img);
		}

		planRepository.save(plan);
	}

	@Transactional
	public void addPlanImg(Long idx, UploadImageRequestBody requestBody) throws NullPointerException {
		Optional<Plan> optPlan = planRepository.findByIdx(idx);

		if (optPlan.isEmpty() || requestBody.getImgUrls() == null)
			throw new NullPointerException("plan이 존재하지 않거나 이미지가 없습니다.");

		savePlanImg(optPlan.get(), requestBody.getImgUrls());

		//TODO return
	}
}
