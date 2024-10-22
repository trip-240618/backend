package com.ll.trip.domain.trip.planP.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.planP.dto.PlanPCheckBoxResponseDto;
import com.ll.trip.domain.trip.planP.dto.PlanPDayDto;
import com.ll.trip.domain.trip.planP.dto.PlanPInfoDto;
import com.ll.trip.domain.trip.planP.dto.PlanPOrderDto;
import com.ll.trip.domain.trip.planP.dto.PlanPWeekDto;
import com.ll.trip.domain.trip.planP.entity.PlanP;
import com.ll.trip.domain.trip.planP.repository.PlanPRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.websoket.response.SocketResponseBody;
import com.ll.trip.global.handler.exception.NoSuchDataException;
import com.ll.trip.global.handler.exception.PermissionDeniedException;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanPService {

	private final PlanPRepository planPRepository;
	private final EntityManager entityManager;
	private final SimpMessagingTemplate template;

	@Transactional
	public PlanP createPlanP(long tripId, PlanPInfoDto requestDto) {
		Trip trip = entityManager.getReference(Trip.class, tripId);

		PlanP plan = PlanP.builder()
			.trip(trip)
			.dayAfterStart(requestDto.getDayAfterStart())
			.content(requestDto.getContent())
			.orderByDate(
				getNextIdx(trip.getId(), requestDto.getDayAfterStart(), requestDto.isLocker())
			)
			.checkbox(false)
			.locker(requestDto.isLocker())
			.build();

		return planPRepository.save(plan);
	}

	@Transactional(readOnly = true)
	public int getNextIdx(long tripId, Integer day, boolean locker) {
		Integer maxOrder = planPRepository.findMaxOrder(tripId, day, locker);
		return maxOrder == null ? 0 : maxOrder + 1_048_576;
	}

	public PlanPInfoDto convertPlanPToDto(PlanP plan) {
		return new PlanPInfoDto(
			plan.getId(),
			plan.getDayAfterStart(),
			plan.getOrderByDate(),
			plan.getContent(),
			plan.isCheckbox(),
			plan.isLocker()
		);
	}

	@Transactional(readOnly = true)
	public List<PlanPDayDto<PlanPInfoDto>> findAllByTripId(long tripId, Integer week, boolean locker) {
		List<PlanPInfoDto> dtos;
		if (locker) {
			dtos = planPRepository.findAllLockerByTripId(tripId);
		} else {
			int dayFrom = (week - 1) * 7 + 1;
			int dayTo = week * 7;
			dtos = planPRepository.findAllByTripId(tripId, dayFrom, dayTo);
		}

		return parseToListResponse(dtos);
	}

	private List<PlanPDayDto<PlanPInfoDto>> parseToListResponse(List<PlanPInfoDto> dtos) {
		List<PlanPDayDto<PlanPInfoDto>> response = new ArrayList<>();
		Map<Integer, List<PlanPInfoDto>> dayMap = new HashMap<>();

		for (PlanPInfoDto dto : dtos) {
			dayMap.computeIfAbsent(dto.getDayAfterStart(), day -> {
				PlanPDayDto<PlanPInfoDto> listDto = new PlanPDayDto<>(day);
				response.add(listDto);
				return listDto.getPlanList();
			}).add(dto);
		}

		return response;
	}

	@Transactional
	public PlanPInfoDto updatePlanPByPlanId(PlanPInfoDto requestBody) {
		long planId = requestBody.getPlanId();
		planPRepository.modifyPlanP(planId, requestBody.getContent(), requestBody.isCheckbox());
		return requestBody;
	}

	@Transactional
	public void deletePlanPByPlanId(int day, long planId) {
		planPRepository.deleteByIdAndDayAfterStart(planId, day);
	}

	@Transactional
	public PlanPCheckBoxResponseDto updateCheckBoxById(Long planId) {
		PlanP planp = planPRepository.findById(planId).orElseThrow(() -> new NoSuchDataException("no such plan"));
		int cnt = planPRepository.updateCheckBoxByPlanId(planId, !planp.isCheckbox());

		if (cnt == 0)
			return null;

		return new PlanPCheckBoxResponseDto(planId, planp.getDayAfterStart(), !planp.isCheckbox());
	}

	@Transactional
	public void moveLocker(long tripId, long planId, Integer dayTo, boolean locker) {
		PlanP plan = planPRepository.findPlanPById(planId)
			.orElseThrow(() -> new NoSuchDataException("plan을 찾을 수 없습니다. planId: " + planId));
		if (plan.isLocker() == locker)
			return;
		Integer dayFrom = plan.getDayAfterStart();

		planPRepository.moveLocker(planId, dayTo, getNextIdx(tripId, dayTo, locker), locker);
		PlanPInfoDto response = convertPlanPToDto(plan);
		response.setLocker(locker);
		response.setDayAfterStart(dayTo);

		if (locker)
			template.convertAndSend("/topic/api/trip/p/" + tripId,
				new SocketResponseBody<>("delete", Map.of("dayAfterStart", dayFrom, "planId", planId)));
		else
			template.convertAndSend("/topic/api/trip/p/" + tripId,
				new SocketResponseBody<>("create", response));
	}

	@Transactional
	public PlanPWeekDto<PlanPInfoDto> movePlanByDayAndOrder(long tripId, PlanPWeekDto<PlanPOrderDto> request) {
		PlanPWeekDto<PlanPInfoDto> response = new PlanPWeekDto<>(request.getWeek());
		List<PlanPDayDto<PlanPInfoDto>> dayList = findAllByTripId(tripId, request.getWeek(), false); //db
		Map<Long, PlanPInfoDto> idMap = new HashMap<>(); // db
		Map<Integer, List<PlanPInfoDto>> dayMap = new HashMap<>(); //response

		for (PlanPDayDto<PlanPInfoDto> dayDto : dayList) {
			PlanPDayDto<PlanPInfoDto> responseDay = new PlanPDayDto<>(dayDto.getDay());
			response.getDayList().add(responseDay); //새로운 day 생성
			dayMap.put(dayDto.getDay(), responseDay.getPlanList()); //db의 데이터에 기반해서 day생성
			for (PlanPInfoDto dto : dayDto.getPlanList())
				idMap.put(dto.getPlanId(), dto);
		}

		for (PlanPDayDto<PlanPOrderDto> dayDto : request.getDayList()) {
			List<PlanPInfoDto> list = dayMap.getOrDefault(dayDto.getDay(), new ArrayList<>()); //접근을 빠르게 하기 위해 선언
			for (PlanPOrderDto dto : dayDto.getPlanList()) {
				if (!idMap.containsKey(dto.getId())) { //db에는 없는 plan일 경우 응답에 포함하지 않고 넘어감
					continue;
				}
				PlanPInfoDto plan = idMap.remove(dto.getId()); //응답에 포함된 plan은 idMap에서 제거
				plan.setOrderByDate(dto.getOrderByDate());
				plan.setDayAfterStart(dto.getDayAfterStart());
				list.add(plan);
			}
		}

		for (PlanPInfoDto dto : idMap.values()) { //요청에는 없지만 db에는 있는 plan
			List<PlanPInfoDto> sortedList = dayMap.get(dto.getDayAfterStart());
			int index = Collections.binarySearch(sortedList, dto,
				((o1, o2) -> {
					if (o1.getOrderByDate() == null)
						return -1;
					return o1.getOrderByDate() - o2.getOrderByDate();
				}));
			if (index < 0)
				index = -(index + 1);
			sortedList.add(index, dto);
		}

		for (List<PlanPInfoDto> list : dayMap.values()) {
			if (list.isEmpty())
				continue;
			List<PlanPInfoDto> updateList = new ArrayList<>();
			PlanPInfoDto firstDto = list.get(0);
			Integer pre = firstDto.getOrderByDate();
			if (pre == null) {
				firstDto.setOrderByDate(0);
				updateList.add(firstDto);
				pre = 0;
			}

			Queue<PlanPInfoDto> nullQue = new LinkedList<>();
			for (PlanPInfoDto dto : list) {
				if (dto.getOrderByDate() == null) {
					nullQue.add(dto);
					continue;
				}
				int last = dto.getOrderByDate();
				if (last - pre <= nullQue.size()) {
					pre = -1;
					break;
				}

				int gap = (last - pre) / (nullQue.size() + 1);
				while (!nullQue.isEmpty()) {
					PlanPInfoDto plan = nullQue.poll();
					pre += gap;
					plan.setOrderByDate(pre);
					updateList.add(plan);
				}
				pre = last;
			}

			if (pre == -1) {
				bulkResetOrder(list);
				continue;
			}

			while (!nullQue.isEmpty()) {
				int gap = 1_048_576;
				PlanPInfoDto plan = nullQue.poll();
				pre += gap;
				plan.setOrderByDate(pre);
				updateList.add(plan);
			}
			bulkUpdateOrder(updateList);
		}

		return response;
	}

	@Transactional
	public int updateOrder(long planId, Integer order) {
		return planPRepository.updateOrderByPlanId(planId, order);
	}

	@Transactional
	public void bulkUpdateOrder(List<PlanPInfoDto> list) {
		int updateCnt = 0;
		for (PlanPInfoDto dto : list) {
			updateCnt += updateOrder(dto.getPlanId(), dto.getOrderByDate());
		}
		if (updateCnt != list.size())
			throw new PermissionDeniedException("순서 변경 실패: " + (list.size() - updateCnt));
	}

	@Transactional
	public void bulkResetOrder(List<PlanPInfoDto> list) {
		int order = 0;
		int updateCnt = 0;
		for (PlanPInfoDto dto : list) {
			dto.setOrderByDate(order);
			updateCnt += updateOrder(dto.getPlanId(), order);
			order += 1_048_576;
		}
		if (updateCnt != list.size())
			throw new PermissionDeniedException("순서 변경 실패 개수: " + (list.size() - updateCnt));
	}

}
