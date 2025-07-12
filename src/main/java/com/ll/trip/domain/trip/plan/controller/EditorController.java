package com.ll.trip.domain.trip.plan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.plan.dto.EditorCheckDto;
import com.ll.trip.domain.trip.plan.service.PlanEditService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Editor")
public class EditorController {

	private final PlanEditService planEditService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/show/editor")
	@Operation(summary = "전체 Plan 편집자 목록")
	public ResponseEntity<EditorCheckDto> showEditors() {
		EditorCheckDto response = new EditorCheckDto(
			planEditService.getSessionIdMap(),
			planEditService.getDestinationMap()
		);
		return ResponseEntity.ok(response);
	}
}
