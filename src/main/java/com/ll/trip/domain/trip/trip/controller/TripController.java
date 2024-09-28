package com.ll.trip.domain.trip.trip.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.file.file.service.AwsAuthService;
import com.ll.trip.domain.notification.notification.service.NotificationService;
import com.ll.trip.domain.trip.trip.dto.TripCreateDto;
import com.ll.trip.domain.trip.trip.dto.TripCreateResponseDto;
import com.ll.trip.domain.trip.trip.dto.TripInfoDto;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.domain.trip.websoket.response.SocketResponseBody;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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
@Tag(name = "Trip", description = "여행방 API")
public class TripController {

	private final TripService tripService;
	private final AwsAuthService awsAuthService;
	private final EntityManager entityManager;
	private final SimpMessagingTemplate template;
	private final NotificationService notificationService;

	@PostMapping("/create")
	@Operation(summary = "여행방 생성")
	@ApiResponse(responseCode = "200", description = "여행방 생성", content = {
		@Content(mediaType = "application/json",
			schema = @Schema(implementation = TripCreateResponseDto.class)
		)})
	public ResponseEntity<?> createTrip(
		@RequestBody TripCreateDto tripCreateDto,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		String invitationCode = tripService.generateInvitationCode();
		Trip trip = tripService.createTrip(tripCreateDto, invitationCode);
		UserEntity user = entityManager.getReference(UserEntity.class, securityUser.getId());

		tripService.joinTripById(trip, user, true);
		notificationService.tripCreateNotifictaion(trip, user);
		return ResponseEntity.ok(new TripCreateResponseDto(trip.getId(), invitationCode));
	}

	@PostMapping("/join")
	@Operation(summary = "여행방 참가")
	@ApiResponse(responseCode = "200", description = "여행방 참가", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = TripInfoDto.class))})
	public ResponseEntity<?> joinTrip(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "초대코드", example = "1A2B3C4D") String invitationCode
	) {
		long tripId = tripService.findTripIdByInvitationCode(invitationCode);
		Trip trip = entityManager.getReference(Trip.class, tripId);
		UserEntity user = entityManager.getReference(UserEntity.class, securityUser.getId());

		boolean isNewMember = tripService.joinTripById(trip, user, false);

		TripInfoDto response = new TripInfoDto(tripService.findTripDetailByTripId(tripId));
		notificationService.tripJoinNotifictaion(trip, trip.getName(), user.getId(), securityUser.getNickname());

		if (isNewMember)
			template.convertAndSend(
				"/topic/api/trip/" + Character.toLowerCase(response.getType()) + "/" + response.getId(),
				new SocketResponseBody<>("member add",
					response.getTripMemberDtoList().get(response.getTripMemberDtoList().size() - 1)));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/enter")
	@Operation(summary = "여행방 입장")
	@ApiResponse(responseCode = "200", description = "여행방 입장", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = TripInfoDto.class))})
	public ResponseEntity<?> enterTrip(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "트립 id", example = "1") long tripId
	) {
		if (!tripService.existTripMemberByTripIdAndUserId(tripId, securityUser.getId()))
			return ResponseEntity.badRequest().body("입장 권한이 없습니다.");

		TripInfoDto response = new TripInfoDto(tripService.findTripDetailByTripId(tripId));

		return ResponseEntity.ok(response);
	}

	@GetMapping("/list/incoming")
	@Operation(summary = "다가오는 Trip리스트 요청")
	@ApiResponse(responseCode = "200", description = "다가오는 Trip리스트 요청", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripInfoDto.class)))})
	public ResponseEntity<?> showIncomingTripList(
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		List<TripInfoDto> response = tripService.findAllIncomingByUserId(securityUser.getId(), LocalDate.now());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/list/last")
	@Operation(summary = "지난 Trip리스트 요청")
	@ApiResponse(responseCode = "200", description = "지난 Trip리스트 요청", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripInfoDto.class)))})
	public ResponseEntity<?> showLastTripList(
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		List<TripInfoDto> response = tripService.findAllLastByUserIdAndDate(securityUser.getId(), LocalDate.now());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/list/bookmark")
	@Operation(summary = "Trip 북마크 리스트 요청")
	@ApiResponse(responseCode = "200", description = "Trip 북마크 리스트 요청", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripInfoDto.class)))})
	public ResponseEntity<?> showBookmarkTripList(
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		List<TripInfoDto> response = tripService.findBookmarkByUserId(securityUser.getId());
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Trip 삭제")
	@ApiResponse(responseCode = "200", description = "Trip 삭제")
	public ResponseEntity<?> deleteTrip(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "트립 id", example = "1") long tripId
	) {
		if (!tripService.isLeaderOfTrip(securityUser.getId(), tripId))
			return ResponseEntity.badRequest().body("해당 여행방에 대한 수정/삭제 권한이 없습니다.");

		List<String> urls = tripService.findImageByTripId(tripId);
		List<String> keys = awsAuthService.extractKeyFromUrl(urls);

		awsAuthService.deleteObjectByKey(keys);

		tripService.deleteTripById(tripId);

		return ResponseEntity.ok("deleted");
	}

	@PutMapping("/modify")
	@Operation(summary = "Trip 수정")
	@ApiResponse(responseCode = "200", description = "Trip 수정", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = TripInfoDto.class))})
	public ResponseEntity<?> modifyTrip(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "트립 id", example = "1") long tripId,
		@RequestBody TripInfoDto requestBody
	) {
		if (!tripService.isLeaderOfTrip(securityUser.getId(), tripId))
			return ResponseEntity.badRequest().body("해당 여행방에 대한 수정/삭제 권한이 없습니다.");
		TripInfoDto response = tripService.modifyTripByDto(entityManager.getReference(Trip.class, tripId), requestBody);

		return ResponseEntity.ok(response);
	}

	//북마크
	@PutMapping("/bookmark/toggle")
	@Operation(summary = "Trip 북마크 토글")
	@ApiResponse(responseCode = "200", description = "Trip 북마크 토글", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))})
	public ResponseEntity<?> toggleTripBookmark(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "트립 id", example = "1") long tripId
	) {
		int update = tripService.toggleBookmarkByTripAndUserId(securityUser.getId(), tripId);
		if (update == 0)
			tripService.createTripBookmark(
				entityManager.getReference(UserEntity.class, securityUser.getId()),
				entityManager.getReference(Trip.class, tripId)
			);

		return ResponseEntity.ok(tripService.getIsToggleByUserIdAndScrapId(securityUser.getId(), tripId));
	}
}
