package com.ll.trip.domain.plan.room.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.plan.room.service.PlanRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlanRoomController {
	private final PlanRoomService planRoomService;

	@PostMapping("/plan/create/room")
	public ResponseEntity<?> createPlanRoom() {
		Long roomId = planRoomService.createRoom();
		return ResponseEntity.ok(roomId);
	}

}
