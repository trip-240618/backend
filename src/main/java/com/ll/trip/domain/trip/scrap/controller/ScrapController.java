package com.ll.trip.domain.trip.scrap.controller;

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

import com.ll.trip.domain.file.file.service.AwsAuthService;
import com.ll.trip.domain.trip.scrap.dto.ScrapCreateDto;
import com.ll.trip.domain.trip.scrap.dto.ScrapDetailDto;
import com.ll.trip.domain.trip.scrap.dto.ScrapListDto;
import com.ll.trip.domain.trip.scrap.dto.ScrapModifyDto;
import com.ll.trip.domain.trip.scrap.entity.Scrap;
import com.ll.trip.domain.trip.scrap.service.ScrapService;
import com.ll.trip.domain.trip.trip.service.TripService;
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
@Tag(name = "Scrap", description = "스크랩 API")
public class ScrapController {
	private final ScrapService scrapService;
	private final TripService tripService;
	private final AwsAuthService awsAuthService;

	@PostMapping("/{tripId}/scrap/create")
	@Operation(summary = "스크랩 생성")
	@ApiResponse(responseCode = "200", description = "스크랩 생성", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScrapDetailDto.class))})
	public ResponseEntity<?> createScrap(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestBody ScrapCreateDto scrapCreateDto
	) {
		if (!tripService.existTripMemberByTripIdAndUserId(tripId, securityUser.getId()))
			return ResponseEntity.badRequest().body("해당 여행방에 대한 권한이 없습니다.");

		Scrap scrap = scrapService.createScrap(
			securityUser.getUuid(), tripId, scrapCreateDto.getTitle(), scrapCreateDto.getContent(),
			scrapCreateDto.getColor(), scrapCreateDto.isHasImage(), scrapCreateDto.getPhotoList()
		);

		return ResponseEntity.ok(new ScrapDetailDto(
			scrap.getId(), securityUser.getUuid(), securityUser.getNickname(), scrap.getTitle(),
			scrap.getContent(), scrap.isHasImage(), scrap.getColor(),
			false, scrap.getCreateDate()
		));
	}

	@GetMapping("/{tripId}/scrap/detail/{scrapId}")
	@Operation(summary = "스크랩 생성")
	@ApiResponse(responseCode = "200", description = "스크랩 생성", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScrapDetailDto.class))})
	public ResponseEntity<?> showScrapDetail(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long scrapId
	) {
		if (!tripService.existTripMemberByTripIdAndUserId(tripId, securityUser.getId()))
			return ResponseEntity.badRequest().body("해당 여행방에 대한 권한이 없습니다.");

		ScrapDetailDto response = scrapService.findByIdWithScrapImage(scrapId, securityUser.getId());
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{tripId}/scrap/modify")
	@Operation(summary = "스크랩 수정")
	@ApiResponse(responseCode = "200", description = "스크랩 수정", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScrapDetailDto.class))})
	public ResponseEntity<?> modifyScrap(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestBody ScrapModifyDto modifyDto
	) {
		if (!scrapService.existByScrapIdAndUuid(modifyDto.getId(), securityUser.getUuid()))
			return ResponseEntity.badRequest().body("해당 스크랩에 대한 수정/삭제 권한이 없습니다.");

		Scrap scrap = scrapService.modifyScrap(
			modifyDto.getId(), modifyDto.getTitle(), modifyDto.getContent(),
			modifyDto.getColor(), modifyDto.isHasImage()
		);

		return ResponseEntity.ok(new ScrapDetailDto(
			scrap.getId(), securityUser.getUuid(), securityUser.getNickname(), scrap.getTitle(),
			scrap.getContent(), scrap.isHasImage(), scrap.getColor(),
			modifyDto.isBookmark(), scrap.getCreateDate()
		));
	}

	@DeleteMapping("/{tripId}/scrap/delete")
	@Operation(summary = "스크랩 삭제")
	@ApiResponse(responseCode = "200", description = "스크랩 삭제",
		content = {
			@Content(mediaType = "application/json",
				examples = {@ExampleObject(name = "삭제 완료시 응답", value = "{\"deleted\"")})})
	public ResponseEntity<?> deleteScrap(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestParam @Parameter(description = "스크랩 pk", example = "1")
		long scrapId
	) {
		if (!scrapService.existByScrapIdAndUuid(scrapId, securityUser.getUuid()))
			return ResponseEntity.badRequest().body("해당 스크랩에 대한 수정/삭제 권한이 없습니다.");
		awsAuthService.deleteImagesByScrapId(scrapId);
		scrapService.deleteById(scrapId);

		return ResponseEntity.ok("deleted");
	}

	@PostMapping("/{tripId}/scrap/bookmark/toggle")
	@Operation(summary = "스크랩 생성")
	@ApiResponse(responseCode = "200", description = "스크랩 생성", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))})
	public ResponseEntity<?> toggleBookmark(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestParam @Parameter(description = "토글할 스크랩 pk", example = "1") long scrapId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		int update = scrapService.toggleScrapBookmark(securityUser.getId(), scrapId);
		if (update == 0) {
			scrapService.createScrapBookmark(securityUser.getId(), scrapId, tripId);
			return ResponseEntity.ok(true);
		}

		boolean response = scrapService.getIsToggleByUserIdAndScrapId(securityUser.getId(), scrapId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{tripId}/scrap/list")
	@Operation(summary = "스크랩 목록")
	@ApiResponse(responseCode = "200", description = "스크랩 목록", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ScrapListDto.class)))})
	public ResponseEntity<?> showScrapList(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		List<ScrapListDto> response = scrapService.findAllByTripIdAndUserId(tripId, securityUser.getId());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{tripId}/scrap/bookmark/list")
	@Operation(summary = "스크랩 북마크 목록")
	@ApiResponse(responseCode = "200", description = "스크랩 북마크 목록", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ScrapListDto.class)))})
	public ResponseEntity<?> showScrapBookmarkList(
		@PathVariable @Parameter(description = "트립 id", example = "1", in = ParameterIn.PATH) long tripId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		List<ScrapListDto> response = scrapService.findAllBookmarkByTripIdAndUserId(tripId, securityUser.getId());
		return ResponseEntity.ok(response);
	}
}
