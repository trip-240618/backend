package com.ll.trip.domain.trip.trip.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.file.file.service.AwsAuthService;
import com.ll.trip.domain.trip.trip.dto.TripCreateDto;
import com.ll.trip.domain.trip.trip.dto.TripInfoDto;
import com.ll.trip.domain.trip.trip.dto.TripMemberDto;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.service.UserService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/trip")
@Tag(name = "Trip", description = "여행방 API")
public class TripController {

	private final TripService tripService;
	private final UserService userService;
	private final AwsAuthService awsAuthService;

	@PostMapping("/create")
	@Operation(summary = "여행방 생성")
	@ApiResponse(responseCode = "200", description = "여행방 생성", content = {
		@Content(mediaType = "application/json",
			examples = @ExampleObject(value = "1A2B3C4D"),
			schema = @Schema(implementation = String.class)
		)})
	public ResponseEntity<?> createTrip(
		@RequestBody TripCreateDto tripCreateDto,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		String invitationCode = tripService.generateInvitationCode();
		Trip trip = tripService.createTrip(tripCreateDto, invitationCode);
		UserEntity user = userService.findUserByUserId(securityUser.getId());

		tripService.joinTripById(trip, user, true);

		return ResponseEntity.ok(invitationCode);
	}

	@PostMapping("/join")
	@Operation(summary = "여행방 참가")
	@ApiResponse(responseCode = "200", description = "여행방 참가", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = TripInfoDto.class))})
	public ResponseEntity<?> joinTripByInvitationCode(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @NotBlank @Parameter(description = "초대 코드", example = "1A2B3C4D") String invitationCode
	) {
		Trip trip = tripService.findByInvitationCode(invitationCode);
		UserEntity user = userService.findUserByUserId(securityUser.getId());

		tripService.joinTripById(trip, user, false);

		TripInfoDto response = new TripInfoDto(trip);
		List<TripMemberDto> tripMemberDtoList = tripService.findTripMemberUserByTripId(trip.getId());
		response.setTripMemberDtoList(tripMemberDtoList);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/enter")
	@Operation(summary = "여행방 입장")
	@ApiResponse(responseCode = "200", description = "여행방 입장", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = TripInfoDto.class))})
	public ResponseEntity<?> enterTripByInvitationCode(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @NotBlank @Parameter(description = "초대 코드", example = "1A2B3C4D") String invitationCode
	) {
		Trip trip = tripService.findByInvitationCode(invitationCode);
		if (!tripService.existTripMemberByTripIdAndUserId(trip.getId(), securityUser.getId()))
			return ResponseEntity.badRequest().body("입장 권한이 없습니다.");

		TripInfoDto response = new TripInfoDto(trip);
		List<TripMemberDto> tripMemberDtoList = tripService.findTripMemberUserByTripId(trip.getId());
		response.setTripMemberDtoList(tripMemberDtoList);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/list/incoming")
	@Operation(summary = "다가오는 Trip리스트 요청")
	@ApiResponse(responseCode = "200", description = "다가오는 Trip리스트 요청", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripInfoDto.class)))})
	public ResponseEntity<?> showIncomingTripListByUserId(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam(required = false) @Parameter(description = "정렬기준 필드", example = "startDate : 시작 날짜 순, endDate : 끝나는 날짜 순") String sortField,
		@RequestParam(required = false) @Parameter(description = "정렬순서", example = "ASC : 오름차순&오래된순, DESC : 내림차순&최신순") String sortDirection
	) {
		//TODO 북마크, 다가오는, 지난 + queryDSL로 동적 쿼리 생성
		List<TripInfoDto> response = null;
		if (sortDirection == null && sortField == null) {
			response = tripService.findAllByUserId(securityUser.getId(), LocalDate.now(), "incoming",
				"startDate", "DESC");
		} else if (sortDirection != null && sortField != null) {
			tripService.findAllByUserId(securityUser.getId(), LocalDate.now(), "incoming",
				sortDirection, sortField);
		}
		tripService.fillTripMemberToTripInfo(response);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/list/last")
	@Operation(summary = "지난 Trip리스트 요청")
	@ApiResponse(responseCode = "200", description = "지난 Trip리스트 요청", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripInfoDto.class)))})
	public ResponseEntity<?> showLastTripListByUserId(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "정렬기준 필드", example = "startDate : 시작 날짜 순, endDate : 끝나는 날짜 순") String sortField,
		@RequestParam @Parameter(description = "정렬순서", example = "ASC : 오름차순&오래된순, DESC : 내림차순&최신순") String sortDirection
	) {
		//TODO 북마크, 다가오는, 지난 + queryDSL로 동적 쿼리 생성
		List<TripInfoDto> response = null;
		if (sortDirection == null && sortField == null) {
			response = tripService.findAllByUserId(securityUser.getId(), LocalDate.now(), "last",
				"startDate", "DESC");
		} else if (sortDirection != null && sortField != null) {
			tripService.findAllByUserId(securityUser.getId(), LocalDate.now(), "last",
				sortDirection, sortField);
		}
		tripService.fillTripMemberToTripInfo(response);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/list/bookmark")
	@Operation(summary = "지난 Trip리스트 요청")
	@ApiResponse(responseCode = "200", description = "지난 Trip리스트 요청", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripInfoDto.class)))})
	public ResponseEntity<?> showBookmarkTripListByUserId(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "정렬기준 필드", example = "startDate : 시작 날짜 순, endDate : 끝나는 날짜 순") String sortField,
		@RequestParam @Parameter(description = "정렬순서", example = "ASC : 오름차순&오래된순, DESC : 내림차순&최신순") String sortDirection
	) {
		//TODO 북마크, 다가오는, 지난 + queryDSL로 동적 쿼리 생성
		List<TripInfoDto> response = tripService.findBookmarkByUserId(securityUser.getId(), sortDirection, sortField);
		tripService.fillTripMemberToTripInfo(response);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/delete")
	@Operation(summary = "Trip 삭제")
	@ApiResponse(responseCode = "200", description = "Trip 삭제")
	public ResponseEntity<?> deleteTripByInvitationCode(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "초대코드", example = "1A2B3C4D") String invitationCode
	) {
		Trip trip = tripService.findByInvitationCode(invitationCode);

		List<String> urls = awsAuthService.abstractUrlFromPresignedUrl(List.of(trip.getThumbnail()));
		List<String> keys = awsAuthService.abstractKeyFromUrl(urls);

		awsAuthService.deleteObjectByKey(keys);

		tripService.deleteTripById(trip.getId());

		return ResponseEntity.ok("deleted");
	}

	@PostMapping("/modify")
	@Operation(summary = "Trip 수정")
	@ApiResponse(responseCode = "200", description = "Trip 수정", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = TripInfoDto.class))})
	public ResponseEntity<?> modifyTripByInvitationCode(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody TripInfoDto requestBody
	) {
		Trip trip = tripService.findByInvitationCode(requestBody.getInvitationCode());

		TripInfoDto response = tripService.modifyTripByDto(trip, requestBody);

		return ResponseEntity.ok(response);
	}

	//북마크
	@PostMapping("/bookmark/toggle")
	@Operation(summary = "Trip 북마크 토글")
	@ApiResponse(responseCode = "200", description = "Trip 북마크 토글", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))})
	public ResponseEntity<?> toggleTripBookmarkByInvitationCode(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "초대코드", example = "1A2B3C4D") String invitationCode
	) {
		Trip trip = tripService.findByInvitationCode(invitationCode);
		//날짜 수정시 기존 플랜변경은 미정
		boolean response = tripService.toggleBookmarkByTripAndUserId(trip, securityUser.getId());

		return ResponseEntity.ok(response);
	}
}
