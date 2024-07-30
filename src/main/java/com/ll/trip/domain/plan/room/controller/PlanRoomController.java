package com.ll.trip.domain.plan.room.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.file.file.dto.UploadImageRequestBody;
import com.ll.trip.domain.plan.room.service.PlanRoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PlanRoomController {
	private final PlanRoomService planRoomService;

	@PostMapping("/plan/room/create")
	public ResponseEntity<?> createPlanRoom() {
		Long roomId = planRoomService.createRoom();
		return ResponseEntity.ok(roomId);
	}

	@PostMapping("/plan/room/{roomId}/upload")
	@Operation(summary = "임시 이미지 업로드")
	@ApiResponse(responseCode = "200", description = "플랜룸에 임시로 이미지를 업로드(*presignedUrl을 받고 파일을 업로드 한 이후에 url만 등록하는 요청)", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
	public ResponseEntity<?> uploadToRoom(
		@PathVariable final Long roomId,
		@RequestBody final UploadImageRequestBody request
	) {
		if (request.getImgUrls() == null)
			return ResponseEntity.badRequest().body("요청바디가 비어있습니다.");

		try {
			planRoomService.uploadImgUrlByRoomId(roomId, request);
		} catch (NullPointerException npe) {
			return ResponseEntity.badRequest().body("방을 찾을 수 없습니다.");
		}

		return ResponseEntity.ok("uploaded");
	}

}
