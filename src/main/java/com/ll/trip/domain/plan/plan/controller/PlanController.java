package com.ll.trip.domain.plan.plan.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ll.trip.domain.file.file.dto.UploadImageRequestBody;
import com.ll.trip.domain.plan.plan.dto.PlanCreateRequestDto;
import com.ll.trip.domain.plan.plan.dto.PlanCreateResponseDto;
import com.ll.trip.domain.plan.plan.dto.PlanDeleteRequestDto;
import com.ll.trip.domain.plan.plan.response.PlanResponseBody;
import com.ll.trip.domain.plan.plan.service.PlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PlanController {

	private final PlanService planService;

	private final SimpMessagingTemplate template;

	@GetMapping("/plan/{roomId}/history")
	@Operation(summary = "기존 plan 요청")
	@ApiResponse(responseCode = "200", description = "db에 저장되어 있는 plan 반환", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlanCreateResponseDto.class)))})
	public ResponseEntity<?> sendPreviousPlans(@PathVariable Long roomId) {
		List<PlanCreateResponseDto> plans = planService.getPreviousMessages(roomId);
		return ResponseEntity.ok(plans);
	}

	@GetMapping("/plan/{roomId}/update/order/possible")
	@Operation(summary = "plan swap 가능 여부 요청 (swap 요청하기 전 필수)")
	@ApiResponse(responseCode = "200", description = "현재 방에 swap중인 유저가 있는지 확인 후 swap중인 유저로 등록", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
	public ResponseEntity<?> sendSwapPossible(@PathVariable Long roomId) {
		//TODO 유저정보로 해당 유저가 교환하는게 맞는지 확인하고 교환해주기
		if (!planService.addSwapUserIfPossible(roomId))
			return ResponseEntity.ok("impossible");
		return ResponseEntity.ok("possible");
	}

	@GetMapping("/plan/{roomId}/update/order/cancel")
	@Operation(summary = "plan swap 취소")
	@ApiResponse(responseCode = "200", description = "메모리에서 roomId에 해당하는 swapUser 삭제", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
	public ResponseEntity<?> sendSwapCancle(@PathVariable Long roomId) {
		//TODO 유저정보로 해당 유저가 교환하는게 맞는지 확인하고 교환해주기
		planService.deleteSwapUser(roomId);
		return ResponseEntity.ok("canceled");
	}

	@GetMapping("/plan/check/swapUser")
	@Operation(summary = "swapUser 메모리 확인")
	@ApiResponse(responseCode = "200", description = "메모리에 등록되어 있는 swapUser 확인", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))})
	public ResponseEntity<?> checkSwapUser() {
		//TODO 유저정보로 해당 유저가 교환하는게 맞는지 확인하고 교환해주기
		Map<Long,String> swapUser = planService.showSwapUser();
		return ResponseEntity.ok(swapUser);
	}

	@PostMapping("/plan/{roomId}/upload/{idx}")
	@Operation(summary = "plan에 이미지 업로드")
	@ApiResponse(responseCode = "200", description = "roomId, idx로 plan을 특정해 이미지를 업로드 (*presignedUrl을 받고 파일을 업로드 한 이후에 url만 등록하는 요청)", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
	public ResponseEntity<?> uploadImage(
		@PathVariable final Long roomId,
		@PathVariable final Long idx,
		@RequestBody UploadImageRequestBody requestBody
	) {
		try {
			planService.addPlanImg(idx, requestBody);
		} catch (NullPointerException npe) {
			return ResponseEntity.badRequest().body("plan이 존재하지 않거나 이미지가 없습니다.");
		}

		template.convertAndSend(
			"/topic/api/plan/" + roomId,
			new PlanResponseBody<>("addImg", requestBody)
		);
		return ResponseEntity.ok("uploaded");
	}

	@MessageMapping("/plan/{roomId}/create")
	@Operation(summary = "plan 생성", description = "command: create , roomId에 plan을 생성",
		responses = {
			@ApiResponse(responseCode = "200", description = "Order updated successfully",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlanResponseBody.class)))
		}
	)
	public void handlePlanCreate(
		PlanCreateRequestDto requestDto,
		@DestinationVariable Long roomId
	) {
		log.info("title: " + requestDto.getTitle());
		PlanCreateResponseDto response = planService.savePlan(roomId, requestDto);
		template.convertAndSend(
			"/topic/api/plan/" + roomId,
			new PlanResponseBody<>("create", response)
		);
	}

	@MessageMapping("/plan/{roomId}/delete")
	public void handlePlanDelete(
		PlanDeleteRequestDto requestDto,
		@DestinationVariable Long roomId
	) {
		log.info("idx: " + requestDto.getIdx());
		long idx = planService.deletePlan(roomId, requestDto);
		if( idx == -1) {
			return;
		}

		template.convertAndSend(
			"/topic/api/plan/" + roomId,
			new PlanResponseBody<>("delete", idx)
		);
	}

	@MessageMapping("/plan/{roomId}/update/order")
	@Operation(summary = "plan 순서 변경", description = "command: swap , roomId 내부의 plan두개의 index를 변경",
		responses = {
			@ApiResponse(responseCode = "200", description = "Order updated successfully",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlanResponseBody.class)))
		}
	)
	public void handleUpdateOrder(
		@DestinationVariable Long roomId,
		List<Long> orders
	) {
		if (planService.swapByIndex(roomId, orders) != 2) {
			template.convertAndSend(
				"/topic/api/plan/" + roomId,
				new PlanResponseBody<>("swap", "Failed")
			);
			return;
		}

		template.convertAndSend(
			"/topic/api/plan/" + roomId,
			new PlanResponseBody<>("swap", orders)
		);

	}

}
