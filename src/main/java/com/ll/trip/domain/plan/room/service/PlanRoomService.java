package com.ll.trip.domain.plan.room.service;

import org.springframework.stereotype.Service;

import com.ll.trip.domain.plan.room.entity.PlanRoom;
import com.ll.trip.domain.plan.room.repository.PlanRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanRoomService {
	private final PlanRoomRepository planRoomRepository;

	public Long createRoom() {
		PlanRoom planRoom = PlanRoom.builder().build();
		planRoom = planRoomRepository.save(planRoom);
		return planRoom.getId();
	}
}
