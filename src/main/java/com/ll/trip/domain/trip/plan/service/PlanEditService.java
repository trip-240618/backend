package com.ll.trip.domain.trip.plan.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.plan.dto.PlanEditDto;
import com.ll.trip.domain.trip.plan.repository.PlanPRepository;
import com.ll.trip.domain.trip.plan.response.PlanResponseBody;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanEditService {

	private final PlanPRepository planPRepository;

	private final String TOPIC_PREFIX = "/topic/api/trip/edit/";
	private final ConcurrentHashMap<String, String> activeEditTopicsAndUuid = new ConcurrentHashMap<>();
	private final SimpMessagingTemplate template;

	public void editorClosedSubscription(String destination, String username) {
		String invitationCode = destination.substring(TOPIC_PREFIX.length());
		if (activeEditTopicsAndUuid.contains(invitationCode) &&
			activeEditTopicsAndUuid.get(invitationCode).equals(username)) {
			activeEditTopicsAndUuid.remove(invitationCode);

			template.convertAndSend(destination, new PlanResponseBody<>("edit finish", username));
		}
	}

	public void addEditor(String invitationCode, String username) {
		activeEditTopicsAndUuid.put(invitationCode, username);
	}

	public String getEditorByInvitationCode(String invitationCode) {
		return activeEditTopicsAndUuid.getOrDefault(invitationCode, null);
	}

	public int movePlanByDayAndOrder(long planId, int dayTo, int orderTo) {
		PlanEditDto planEditDto = planPRepository.findPlanEditDtoById(planId).orElseThrow(NullPointerException::new);

		if (planEditDto.getDayAfterStart() == dayTo) {
			return movePlanPInSameDay(planEditDto, dayTo, orderTo);
		} else {
			return movePlanPInAnotherDay(planEditDto, dayTo, orderTo);
		}
	}

	@Transactional
	public int movePlanPInSameDay(PlanEditDto planEditDto, int dayTo, int orderTo) {
		int order = planEditDto.getOrderByDate();
		int updated = 0;

		if (orderTo > order) {
			updated += planPRepository.reduceOrderFromToByTripIdAndDay(planEditDto.getTripId(), dayTo, order + 1,
				orderTo);
		} else {
			updated += planPRepository.increaseOrderFromToByTripIdAndDay(planEditDto.getTripId(), dayTo, orderTo,
				order - 1);

		}
		updated += planPRepository.updateDayOrderById(planEditDto.getId(), dayTo, orderTo);

		return updated;
	}

	@Transactional
	public int movePlanPInAnotherDay(PlanEditDto planEditDto, int dayTo, int orderTo) {
		long tripId = planEditDto.getTripId();
		int day = planEditDto.getDayAfterStart();
		int order = planEditDto.getOrderByDate();
		int updated = 0;

		updated += planPRepository.reduceOrderFromByTripIdAndDay(tripId, day, order);
		updated += planPRepository.increaseOrderFromByTripIdAndDay(tripId, dayTo, orderTo);
		updated += planPRepository.updateDayOrderById(planEditDto.getId(), dayTo, orderTo);

		return updated;
	}

	public boolean isEditor(String invitationCode, String uuid) {
		return activeEditTopicsAndUuid.get(invitationCode).equals(uuid);
	}
}
