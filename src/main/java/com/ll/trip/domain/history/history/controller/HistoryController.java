package com.ll.trip.domain.history.history.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.ll.trip.domain.history.history.dto.HistoriesCreateRequestDto;
import com.ll.trip.domain.history.history.dto.HistoryCreateRequestDto;
import com.ll.trip.domain.history.history.dto.HistoryDetailDto;
import com.ll.trip.domain.history.history.dto.HistoryListDto;
import com.ll.trip.domain.history.history.dto.HistoryModifyDto;
import com.ll.trip.domain.history.history.dto.HistoryReplyCreateRequestDto;
import com.ll.trip.domain.history.history.dto.HistoryReplyDto;
import com.ll.trip.domain.history.history.dto.HistoryReplyModifyDto;
import com.ll.trip.domain.history.history.dto.HistoryTagDto;
import com.ll.trip.domain.history.history.entity.History;
import com.ll.trip.domain.history.history.entity.HistoryReply;
import com.ll.trip.domain.history.history.service.HistoryService;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.global.handler.dto.ErrorResponseDto;
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
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/trip")
@Tag(name = "History", description = "History API")
public class HistoryController {

	private final TripService tripService;
	private final HistoryService historyService;
	private final EntityManager entityManager;

	@GetMapping("/{tripId}/history/list")
	@Operation(summary = "History 리스트")
	@ApiResponse(responseCode = "200", description = "History 리스트", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryListDto.class)))})
	public ResponseEntity<?> showHistoryList(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!tripService.existTripMemberByTripIdAndUserId(tripId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		List<HistoryListDto> response = historyService.findAllByTripId(tripId);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{tripId}/history/create")
	@Operation(summary = "History 생성")
	@ApiResponse(responseCode = "200", description = "History 생성", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryListDto.class)))})
	public ResponseEntity<?> createHistory(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody HistoryCreateRequestDto requestDto
	) {
		if (!tripService.existTripMemberByTripIdAndUserId(tripId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		UserEntity user = entityManager.getReference(UserEntity.class, securityUser.getId());
		Trip trip = entityManager.getReference(Trip.class, tripId);

		HistoryDetailDto response = historyService.showHistoryDetail(
			historyService.createHistory(requestDto, user, trip).getId(),
			securityUser.getId());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{tripId}/history/create/many")
	@Operation(summary = "History 일괄 생성")
	@ApiResponse(responseCode = "200", description = "History 생성", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryListDto.class)))})
	public ResponseEntity<?> createManyHistories(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody HistoriesCreateRequestDto requestDto
	) {
		if (!tripService.existTripMemberByTripIdAndUserId(tripId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		UserEntity user = entityManager.getReference(UserEntity.class, securityUser.getId());
		Trip trip = entityManager.getReference(Trip.class, tripId);

		List<HistoryListDto> response = historyService.createManyHistories(requestDto.getHistoryCreateRequestDtos(),
			user, trip);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{tripId}/history/modify/{historyId}")
	@Operation(summary = "History 수정")
	@ApiResponse(responseCode = "200", description = "History 수정", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryDetailDto.class)))})
	public ResponseEntity<?> modifyHistory(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody HistoryModifyDto requestDto
	) {
		if (!historyService.isWriterOfHistory(historyId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		historyService.modifyHistory(tripId, historyId, requestDto);
		HistoryDetailDto response = historyService.showHistoryDetail(historyId, securityUser.getId());

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{tripId}/history/delete/{historyId}")
	@Operation(summary = "History 삭제")
	@ApiResponse(responseCode = "200", description = "History 삭제", content = {
		@Content(mediaType = "application/json",
			schema = @Schema(implementation = String.class))})
	public ResponseEntity<?> deleteHistory(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!historyService.isWriterOfHistory(historyId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		historyService.deleteHistory(historyId);

		return ResponseEntity.ok("deleted");
	}

	@GetMapping("/{tripId}/history/detail/{historyId}")
	@Operation(summary = "History 상세")
	@ApiResponse(responseCode = "200", description = "History 상세", content = {
		@Content(mediaType = "application/json",
			schema = @Schema(implementation = HistoryDetailDto.class))})
	public ResponseEntity<?> showHistoryDetail(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!tripService.existTripMemberByTripIdAndUserId(tripId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		HistoryDetailDto response = historyService.showHistoryDetail(historyId, securityUser.getId());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{tripId}/history/{historyId}/reply/create")
	@Operation(summary = "History 댓글 생성")
	@ApiResponse(responseCode = "200", description = "History 댓글 생성 후 댓글목록 반환", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryReplyDto.class)))})
	public ResponseEntity<?> createHistoryReply(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody HistoryReplyCreateRequestDto requestDto
	) {
		if (!tripService.existTripMemberByTripIdAndUserId(tripId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		UserEntity user = entityManager.getReference(UserEntity.class, securityUser.getId());
		History history = entityManager.getReference(History.class, historyId);
		historyService.createHistoryReply(history, user, requestDto);

		List<HistoryReplyDto> response = historyService.showHistoryReplyList(historyId);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{tripId}/history/{historyId}/reply/modify")
	@Operation(summary = "History 댓글 수정")
	@ApiResponse(responseCode = "200", description = "History 댓글 수정 후 댓글목록 반환", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryReplyDto.class)))})
	public ResponseEntity<?> modifyHistoryReply(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody HistoryReplyModifyDto requestDto
	) {
		if (!historyService.isWriterOfReply(requestDto.getReplyId(), securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		historyService.modifyHistoryReply(
			entityManager.getReference(HistoryReply.class, requestDto.getReplyId()),
			requestDto.getContent());

		List<HistoryReplyDto> response = historyService.showHistoryReplyList(historyId);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{tripId}/history/{historyId}/reply/delete")
	@Operation(summary = "History 댓글 삭제")
	@ApiResponse(responseCode = "200", description = "History 댓글 삭제 후 댓글목록 반환", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryReplyDto.class)))})
	public ResponseEntity<?> deleteHistoryReply(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "히스토리 id", example = "1") long replyId
	) {
		if (!historyService.isWriterOfReply(replyId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");
		historyService.deleteHistoryReply(historyId, replyId);

		List<HistoryReplyDto> response = historyService.showHistoryReplyList(historyId);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{tripId}/history/{historyId}/reply/list")
	@Operation(summary = "History 댓글 목록")
	@ApiResponse(responseCode = "200", description = "원하는 History의 댓글 목록 반환", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryReplyDto.class)))})
	public ResponseEntity<?> showHistoryReplyList(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!tripService.existTripMemberByTripIdAndUserId(tripId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		List<HistoryReplyDto> response = historyService.showHistoryReplyList(historyId);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{tripId}/history/{historyId}/like")
	@Operation(summary = "History 좋아요 토글")
	@ApiResponse(responseCode = "200", description = "History 좋아요 토글", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "수정후 좋아요 상태", value = "{\"true\"")},
			schema = @Schema(implementation = Boolean.class))})
	public ResponseEntity<?> toggleHistoryLike(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!tripService.existTripMemberByTripIdAndUserId(tripId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		boolean toggle = historyService.toggleHistoryLike(
			entityManager.getReference(History.class, historyId),
			entityManager.getReference(UserEntity.class, securityUser.getId()));

		return ResponseEntity.ok(toggle);
	}

	@PostMapping("/{tripId}/history/{historyId}/tag/create")
	@Operation(summary = "History 태그 생성")
	@ApiResponse(responseCode = "200", description = "History 태그 생성", content = {
		@Content(mediaType = "application/json",
			schema = @Schema(implementation = HistoryTagDto.class))})
	public ResponseEntity<?> createHistoryTag(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@RequestBody HistoryTagDto requestBody,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!historyService.isWriterOfHistory(historyId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");
		Trip trip = entityManager.getReference(Trip.class, tripId);
		History history = entityManager.getReference(History.class, historyId);

		HistoryTagDto response = new HistoryTagDto(historyService.createHistoryTag(requestBody, trip, history));

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{tripId}/history/{historyId}/tag/delete")
	@Operation(summary = "History 태그 삭제")
	@ApiResponse(responseCode = "200", description = "History 태그 삭제", content = {
		@Content(mediaType = "application/json",
			schema = @Schema(implementation = String.class))})
	public ResponseEntity<?> deleteHistoryTag(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "히스토리 id", example = "1", in = ParameterIn.PATH) long historyId,
		@RequestParam @Parameter(description = "태그 id", example = "1", in = ParameterIn.PATH) long tagId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!historyService.isWriterOfHistory(historyId, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		historyService.deleteHistoryTag(tagId);

		return ResponseEntity.ok("deleted");
	}

	@GetMapping("/{tripId}/history/tags")
	@Operation(summary = "History 모든 태그 목록")
	@ApiResponse(responseCode = "200", description = "여행방에 등록된 모든 태그를 반환", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryListDto.class)))})
	public ResponseEntity<?> searchHistoryTags(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId
	) {
		List<HistoryTagDto> response = historyService.showAllTagsByTripId(tripId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{tripId}/history/search")
	@Operation(summary = "History 검색")
	@ApiResponse(responseCode = "200", description = "주어진 파라미터로 검색 (uuid가 있으면 uuid만 검색)", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = HistoryListDto.class)))})
	public ResponseEntity<?> searchHistory(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestParam(required = false) @Parameter(description = "작성자 uuid", example = "c9f30d9e-0bac-4a81-b005-6a79ba4fbef4", in = ParameterIn.PATH) String uuid,
		@RequestParam(required = false) @Parameter(description = "태그명", example = "# 긴자", in = ParameterIn.PATH) String tagName,
		@RequestParam(required = false) @Parameter(description = "태그 컬러", example = "#FFEFF3", in = ParameterIn.PATH) String tagColor
	) {
		List<HistoryListDto> response = null;
		if(uuid != null) {
			response = historyService.searchHistoryByUuid(tripId, uuid);
		} else if(tagName != null) {
			response = historyService.searchHistoryByTagNameAndColor(tripId, tagName,tagColor);
		} else ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto("Parameter is missing"));

		return ResponseEntity.ok(response);
	}

}
