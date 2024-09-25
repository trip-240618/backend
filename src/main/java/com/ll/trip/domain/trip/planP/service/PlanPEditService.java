package com.ll.trip.domain.trip.planP.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.location.response.PlanResponseBody;
import com.ll.trip.domain.trip.planP.repository.PlanPRepository;

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

	@Transactional
	public int movePlanByDayAndOrder(long tripId, long planId, int dayFrom, int dayTo, int orderFrom, int orderTo) {
		if (dayFrom == dayTo) {
			return movePlanPInSameDay(tripId, planId, dayTo, orderFrom, orderTo);
		} else {
			return movePlanPInAnotherDay(tripId, planId, dayTo, orderTo);
		}
	}

	@Transactional
	public int movePlanPInSameDay(long tripId, long planId, int dayTo, int orderFrom, int orderTo) {
		int updated = 0;

		if (orderTo > orderFrom) {
			updated += planPRepository.reduceOrderFromToByTripIdAndDay(tripId, dayTo, orderFrom + 1,
				orderTo);
		} else {
			updated += planPRepository.increaseOrderFromToByTripIdAndDay(tripId, dayTo, orderTo,
				orderFrom - 1);

		}
		updated += planPRepository.updateDayOrderById(planId, dayTo, orderTo);

		return updated;
	}

	@Transactional
	public int movePlanPInAnotherDay(long tripId, long planId, int dayTo, int orderTo) {
		int updated = 0;

		updated += planPRepository.increaseOrderFromByTripIdAndDay(tripId, dayTo, orderTo);
		updated += planPRepository.updateDayOrderById(planId, dayTo, orderTo);

		return updated;
	}

	public boolean isEditor(String invitationCode, String uuid) {
		return activeEditTopicsAndUuid.get(invitationCode).equals(uuid);
	}

	public void removeEditor(String invitationCode, String uuid) {
		this.activeEditTopicsAndUuid.remove(invitationCode, uuid);
	}
}
