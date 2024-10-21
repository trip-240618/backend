package com.ll.trip.domain.trip.planJ.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.planJ.dto.PlanJCreateRequestDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJListDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJModifyRequestDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJOrderDto;
import com.ll.trip.domain.trip.planJ.entity.PlanJ;
import com.ll.trip.domain.trip.planJ.repository.PlanJRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.global.handler.exception.NoSuchDataException;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanJService {

	private final PlanJRepository planJRepository;
	private final EntityManager entityManager;

	@Transactional
	public void deletePlanJById(Integer day, long planId) {
		planJRepository.deleteByIdAndDayAfterStart(planId, day);
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
			.place(requestDto.getPlace())
			.memo(requestDto.getMemo())
			.title(requestDto.getTitle())
			.locker(requestDto.isLocker())
			.build();

		return planJRepository.save(plan);
	}

	public PlanJInfoDto convertPlanJToDto(PlanJ plan) {
		return new PlanJInfoDto(plan);
	}

	public List<PlanJListDto> findAllPlanAByTripIdAndDay(long tripId, int day) {
		List<PlanJInfoDto> dtos = planJRepository.findAllPlanAByTripIdAndDay(tripId, day, false);
		return parseToListResponse(dtos);
	}

	public List<PlanJListDto> findAllPlanBByTripId(long tripId) {
		List<PlanJInfoDto> dtos = planJRepository.findAllPlanBByTripIdAndDay(tripId, true);
		return parseToListResponse(dtos);
	}

	private List<PlanJListDto> parseToListResponse(List<PlanJInfoDto> dtos) {
		Map<Integer, List<PlanJInfoDto>> dayMap = new HashMap<>();
		List<PlanJListDto> response = new ArrayList<>();

		for (PlanJInfoDto dto : dtos) {
			dayMap.computeIfAbsent(dto.getDayAfterStart(), day -> {
				PlanJListDto listDto = new PlanJListDto(day);
				response.add(listDto);
				return listDto.getPlanList();
			}).add(dto);
		}

		return response;
	}

	@Transactional
	public PlanJ updatePlanJByPlanId(PlanJ plan, PlanJModifyRequestDto requestBody, int order) {
		plan.setTitle(requestBody.getTitle());
		plan.setMemo(requestBody.getMemo());
		plan.setDayAfterStart(requestBody.getDayAfterStart());
		plan.setStartTime(requestBody.getStartTime());
		plan.setLatitude(requestBody.getLatitude());
		plan.setLongitude(requestBody.getLongitude());
		plan.setPlace(requestBody.getPlace());
		plan.setOrderByDate(order);
		plan.setLocker(requestBody.isLocker());

		return planJRepository.save(plan);
	}

	public PlanJ findPlanJById(long planId) {
		return planJRepository.findById(planId).orElseThrow(() -> new NoSuchDataException("대상을 찾을 수 없습니다. planId: " + planId));
	}

	@Transactional
	public void updatePlanJDay(Long tripId, int dayDiffer, int duration) {
		planJRepository.updateDayByTripId(tripId, dayDiffer);
		planJRepository.deleteByTripIdAndDuration(tripId, duration);
	}

	@Transactional
	public List<PlanJListDto> bulkUpdatePlanJOrder(long tripId, int day, List<PlanJOrderDto> orderDtos) {
		List<PlanJInfoDto> planJList = findAllPlanAByTripIdAndDay(tripId, day).get(0).getPlanList();
		Map<Long, PlanJInfoDto> planJMap = new HashMap<>();

		for (PlanJInfoDto plan : planJList) {
			planJMap.put(plan.getPlanId(), plan);
		}

		List<PlanJInfoDto> response = new ArrayList<>();
		for (PlanJOrderDto dto : orderDtos) {
			long planId = dto.getPlanId();
			Integer order = dto.getOrderByDate();
			LocalTime startTime = dto.getStartTime();
			if (order == null || startTime == null || !planJMap.containsKey(planId))
				continue;
			PlanJInfoDto plan = planJMap.remove(planId);
			if (!startTime.equals(plan.getStartTime()) || !order.equals(plan.getOrderByDate())) {
				plan.setStartTime(startTime);
				plan.setOrderByDate(order);
				planJRepository.updateStartTimeAndOrder(planId, startTime, order);
			}
			response.add(plan);
		}
		response.addAll(planJMap.values());

		response.sort(Comparator
			.comparing(PlanJInfoDto::getStartTime)
			.thenComparing(PlanJInfoDto::getOrderByDate));

		//planJRepository.flush(); 배치 추가시 주석해제
		return List.of(new PlanJListDto(day, response));
	}
}
