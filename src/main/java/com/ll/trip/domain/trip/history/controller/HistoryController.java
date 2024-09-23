package com.ll.trip.domain.trip.history.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.history.dto.HistoriesCreateRequestDto;
import com.ll.trip.domain.trip.history.dto.HistoryCreateRequestDto;
import com.ll.trip.domain.trip.history.dto.HistoryDetailDto;
import com.ll.trip.domain.trip.history.dto.HistoryListDto;
import com.ll.trip.domain.trip.history.dto.HistoryReplyCreateRequestDto;
import com.ll.trip.domain.trip.history.dto.HistoryReplyDto;
import com.ll.trip.domain.trip.history.entity.History;
import com.ll.trip.domain.trip.history.service.HistoryService;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.service.UserService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/trip")
@Tag(name = "History", description = "History API")
public class HistoryController {

	private final TripService tripService;
	private final UserService userService;
	private final HistoryService historyService;

	@GetMapping("/{invitationCode}/history/list")
	@Operation(summary = "History 리스트")
	@ApiResponse(responseCode = "200", description = "History 리스트", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryListDto.class)))})
	public ResponseEntity<?> showHistoryList(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!tripService.existTripMemberByTripInvitationCodeAndUserId(invitationCode, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		Trip trip = tripService.findByInvitationCode(invitationCode);
		List<HistoryListDto> response = historyService.findAllByTripId(trip.getId());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{invitationCode}/history/create")
	@Operation(summary = "History 생성")
	@ApiResponse(responseCode = "200", description = "History 생성", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryListDto.class)))})
	public ResponseEntity<?> createHistory(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody HistoryCreateRequestDto requestDto
	) {
		if (!tripService.existTripMemberByTripInvitationCodeAndUserId(invitationCode, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		UserEntity user = userService.findUserByUserId(securityUser.getId());
		Trip trip = tripService.findByInvitationCode(invitationCode);

		historyService.createHistory(requestDto, user, trip);

		return ResponseEntity.ok("created");
	}

	@PostMapping("/{invitationCode}/history/create/many")
	@Operation(summary = "History 일괄 생성")
	@ApiResponse(responseCode = "200", description = "History 생성", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryListDto.class)))})
	public ResponseEntity<?> createManyHistories(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody HistoriesCreateRequestDto requestDto
	) {
		if (!tripService.existTripMemberByTripInvitationCodeAndUserId(invitationCode, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		UserEntity user = userService.findUserByUserId(securityUser.getId());
		Trip trip = tripService.findByInvitationCode(invitationCode);

		historyService.createManyHistories(requestDto.getHistoryCreateRequestDtos(), user, trip);

		return ResponseEntity.ok("created");
	}

	@DeleteMapping("/{invitationCode}/history/delete/{historyId}")
	public ResponseEntity<?> deleteHistory(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!historyService.isWriterOfHistory(historyId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		historyService.deleteHistory(historyId);

		return ResponseEntity.ok("deleted");
	}

	@GetMapping("/{invitationCode}/history/detail/{historyId}")
	public ResponseEntity<?> showHistoryDetail(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!tripService.existTripMemberByTripInvitationCodeAndUserId(invitationCode, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		HistoryDetailDto response = historyService.showHistoryDetail(historyId);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{invitationCode}/history/{historyId}/reply/create")
	@Operation(summary = "History 댓글 생성")
	@ApiResponse(responseCode = "200", description = "History 댓글 생성 후 댓글목록 반환", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryReplyDto.class)))})
	public ResponseEntity<?> createHistoryReply(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody HistoryReplyCreateRequestDto requestDto
	) {
		if (!tripService.existTripMemberByTripInvitationCodeAndUserId(invitationCode, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");
		UserEntity user = userService.findUserByUserId(securityUser.getId());
		History history = historyService.findById(historyId);
		historyService.createHistoryReply(history, user, requestDto);

		List<HistoryReplyDto> response = historyService.showHistoryReplyList(historyId);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{invitationCode}/history/{historyId}/reply/delete")
	@Operation(summary = "History 댓글 삭제")
	@ApiResponse(responseCode = "200", description = "History 댓글 삭제 후 댓글목록 반환", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryReplyDto.class)))})
	public ResponseEntity<?> deleteHistoryReply(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "히스토리 id", example = "1") long replyId
	) {
		if (!historyService.isWriterOfReply(historyId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");
		historyService.deleteHistoryReply(replyId);

		List<HistoryReplyDto> response = historyService.showHistoryReplyList(historyId);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{invitationCode}/history/{historyId}/like")
	@Operation(summary = "History 좋아요 토글")
	@ApiResponse(responseCode = "200", description = "History 좋아요 토글", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "수정후 좋아요 상태", value = "{\"true\"")},
			schema = @Schema(implementation = Boolean.class))})
	public ResponseEntity<?> toggleHistoryLike(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!historyService.isWriterOfReply(historyId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");
		boolean toggle = historyService.toggleHistoryLike(historyId, securityUser.getId());

		return ResponseEntity.ok(toggle);
	}

}
