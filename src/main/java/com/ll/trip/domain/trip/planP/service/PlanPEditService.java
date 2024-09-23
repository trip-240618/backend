package com.ll.trip.domain.trip.planP.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.planP.dto.PlanPEditDto;
import com.ll.trip.domain.trip.planP.repository.PlanPRepository;
import com.ll.trip.domain.trip.location.response.PlanResponseBody;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanPEditService {

	private final PlanPRepository planPRepository;

	private final String TOPIC_PREFIX = "/topic/api/trip/p/";
	@Getter
	private final ConcurrentHashMap<String, String> activeEditTopicsAndUuid = new ConcurrentHashMap<>();
	private final SimpMessagingTemplate template;

	public void editorClosedSubscription(String invitationCode, String username) {
		if (activeEditTopicsAndUuid.contains(invitationCode) &&
			activeEditTopicsAndUuid.get(invitationCode).equals(username)) {
			activeEditTopicsAndUuid.remove(invitationCode);

			template.convertAndSend(TOPIC_PREFIX + invitationCode, new PlanResponseBody<>("edit finish", username));
		}
	}

	public void addEditor(String invitationCode, String username) {
		activeEditTopicsAndUuid.put(invitationCode, username);
	}

	public String getEditorByInvitationCode(String invitationCode) {
		return activeEditTopicsAndUuid.getOrDefault(invitationCode, null);
	}

	public int movePlanByDayAndOrder(long planId, int dayTo, int orderTo) {
		PlanPEditDto planPEditDto = planPRepository.findPlanEditDtoById(planId).orElseThrow(NullPointerException::new);

		if (planPEditDto.getDayAfterStart() == dayTo) {
			return movePlanPInSameDay(planPEditDto, dayTo, orderTo);
		} else {
			return movePlanPInAnotherDay(planPEditDto, dayTo, orderTo);
		}
	}

	@Transactional
	public int movePlanPInSameDay(PlanPEditDto planPEditDto, int dayTo, int orderTo) {
		int orderFrom = planPEditDto.getOrderByDate();
		int updated = 0;

		if (orderTo > orderFrom) {
			updated += planPRepository.reduceOrderFromToByTripIdAndDay(planPEditDto.getTripId(), dayTo, orderFrom + 1,
				orderTo);
		} else {
			updated += planPRepository.increaseOrderFromToByTripIdAndDay(planPEditDto.getTripId(), dayTo, orderTo,
				orderFrom - 1);

		}
		updated += planPRepository.updateDayOrderById(planPEditDto.getId(), dayTo, orderTo);

		return updated;
	}

	@Transactional
	public int movePlanPInAnotherDay(PlanPEditDto planPEditDto, int dayTo, int orderTo) {
		long tripId = planPEditDto.getTripId();
		int day = planPEditDto.getDayAfterStart();
		int order = planPEditDto.getOrderByDate();
		int updated = 0;

		updated += planPRepository.reduceOrderFromByTripIdAndDay(tripId, day, order);
		updated += planPRepository.increaseOrderFromByTripIdAndDay(tripId, dayTo, orderTo);
		updated += planPRepository.updateDayOrderById(planPEditDto.getId(), dayTo, orderTo);

		return updated;
	}

	public boolean isEditor(String invitationCode, String uuid) {
		return activeEditTopicsAndUuid.get(invitationCode).equals(uuid);
	}
}
