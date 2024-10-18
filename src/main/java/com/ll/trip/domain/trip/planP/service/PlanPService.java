package com.ll.trip.domain.trip.planP.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
	public PlanP updatePlanPByPlanId(PlanPInfoDto requestBody) {
		PlanP plan = planPRepository.findPlanPById(requestBody.getPlanId()).orElseThrow(NullPointerException::new);

		plan.setContent(requestBody.getContent());
		plan.setCheckbox(requestBody.isCheckbox());

		return planPRepository.save(plan);
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

		plan.setDayAfterStart(dayTo);
		plan.setOrderByDate(getNextIdx(tripId, dayTo, locker));
		plan.setLocker(locker);
		plan = planPRepository.save(plan);

		if (locker)
			template.convertAndSend("/topic/api/trip/p/" + tripId,
				new SocketResponseBody<>("delete", Map.of("dayAfterStart", dayFrom, "planId", planId)));
		else
			template.convertAndSend("/topic/api/trip/p/" + tripId,
				new SocketResponseBody<>("create", convertPlanPToDto(plan)));

	}

	@Transactional
	public void movePlanByDayAndOrder(long tripId, PlanPWeekDto<PlanPOrderDto> request) {
		PlanPWeekDto<PlanPInfoDto> response = new PlanPWeekDto<>(request.getWeek());
		List<PlanPDayDto<PlanPInfoDto>> dayList = findAllByTripId(tripId, request.getWeek(), false);
		Map<Long, PlanPInfoDto> idMap = new HashMap<>();
		Map<Integer, List<PlanPInfoDto>> dayMap = new HashMap<>();
		for (PlanPDayDto<PlanPInfoDto> dayDto : dayList) {
			dayMap.put(dayDto.getDay(), dayDto.getPlanList());
			for (PlanPInfoDto dto : dayDto.getPlanList())
				idMap.put(dto.getPlanId(), dto);
		}

		for (PlanPDayDto<PlanPInfoDto> dayDto : response.getDayList()) {
			int requestSize = dayDto.getPlanList().size();
			PlanPDayDto<PlanPInfoDto> responseDay = new PlanPDayDto<>(dayDto.getDay());
			response.getDayList().add(responseDay); //새로운 day 생성
			List<PlanPInfoDto> list = responseDay.getPlanList();
			for (PlanPInfoDto dto : dayDto.getPlanList()) {
				if (!idMap.containsKey(dto.getPlanId())) {
					requestSize--;
					continue;
				}

			}

			if (dayList.size() > requestSize) {
				for (int i = requestSize; i < dayList.size(); i++) {
					list.add(dayDto.getPlanList().get(i));
				}
			}
		}

		template.convertAndSend("/topic/api/trip/p/" + tripId, new SocketResponseBody<>("move", response));
	}

}
