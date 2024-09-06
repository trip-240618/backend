package com.ll.trip.domain.trip.planJ.service;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.planJ.dto.PlanJDeleteDto;
import com.ll.trip.domain.trip.planJ.repository.PlanJRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanJEditService {
	private final PlanJRepository planJRepository;

	private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> activeEditTopicsAndUuidAndDay = new ConcurrentHashMap<>();

	public int findAndMovePlanJOrderByStartTimeWhereIdAndDay(long tripId, int dayAfterStart, Integer orderFrom,
		LocalTime startTime) {
		int to = findOrderByDayAndTime(tripId, dayAfterStart, startTime);

		if (orderFrom == null) {
			planJRepository.increaseOrderWhereBiggerThanOrder(tripId, dayAfterStart, to);
			return to;
		}

		int updated = movePlanJInSameDay(tripId, dayAfterStart, orderFrom, to);
		if (updated == 0)
			throw new UnexpectedRollbackException("order 업데이트가 정상적으로 이루어지지 않음");

		return to;
	}

	private int findOrderByDayAndTime(long tripId, int dayAfterStart, LocalTime startTime) {
		Object[] object = planJRepository.findOrderByDayAndStartTime(tripId, dayAfterStart, startTime);
		Integer maxOrder = (Integer)object[0];
		Integer firstBiggerOrder = (Integer)object[1];

		if (maxOrder == null)
			return 0;

		if (firstBiggerOrder == null)
			return maxOrder + 1;

		return firstBiggerOrder;
	}

	@Transactional
	public int movePlanJInSameDay(long tripId, int day, int orderFrom, int orderTo) {
		int updated = 0;

		if (orderTo > orderFrom) {
			updated += planJRepository.reduceOrderFromToByTripIdAndDay(tripId, day, orderFrom + 1,
				orderTo);
		} else {
			updated += planJRepository.increaseOrderFromToByTripIdAndDay(tripId, day, orderTo,
				orderFrom - 1);

		}
		return updated;
	}

	@Transactional
	public int movePlanJAfterDeleteByPlanId(Long planId) {
		PlanJDeleteDto dto = planJRepository.findPlanJDeleteDtoByPlanId(planId).orElseThrow(NullPointerException::new);

		return reduceOrderBiggerThanPlanOrder(dto.getTripId(), dto.getDayAfterStart(),
			dto.getOrderByDate());
	}

	private int reduceOrderBiggerThanPlanOrder(long tripId, int dayAfterStart, int orderByDate) {
		return planJRepository.reduceOrderBiggerThanOrder(tripId, dayAfterStart, orderByDate);

	}

	public String getEditorByInvitationCodeAndDay(String invitationCode, int day) {
		ConcurrentHashMap<String, Integer> map = activeEditTopicsAndUuidAndDay.getOrDefault(invitationCode, null);

		if (map == null)
			return null;

		return map.entrySet()
			.stream()
			.filter(entry -> entry.getValue().equals(day))
			.map(Map.Entry::getKey)
			.findFirst()
			.orElse(null);
	}

	public void addEditor(String invitationCode, String uuid, int day) {
		if (!activeEditTopicsAndUuidAndDay.containsKey(invitationCode)) {
			activeEditTopicsAndUuidAndDay.put(invitationCode, new ConcurrentHashMap<>());
		}

		activeEditTopicsAndUuidAndDay.get(invitationCode).put(uuid, day);
	}

	public boolean isEditor(String invitationCode, String uuid, int day) {
		if (!activeEditTopicsAndUuidAndDay.containsKey(invitationCode))
			return false;
		return activeEditTopicsAndUuidAndDay.get(invitationCode).get(uuid) == day;
	}
}
