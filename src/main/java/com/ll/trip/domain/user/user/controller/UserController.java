package com.ll.trip.domain.user.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.file.file.service.AwsAuthService;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.domain.user.user.dto.UserInfoDto;
import com.ll.trip.domain.user.user.dto.UserModifyDto;
import com.ll.trip.domain.user.user.dto.UserRegisterDto;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.service.UserService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "유저정보 API")
public class UserController {

	private final UserService userService;
	private final TripService tripService;
	private final AwsAuthService awsAuthService;

	@GetMapping("/info")
	@Operation(summary = "유저 정보 반환")
	@ApiResponse(responseCode = "200", description = "유저정보 반환", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class))})
	public ResponseEntity<?> getUserInfo(
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		log.info("uuid : " + securityUser.getUsername());
		UserEntity user = userService.findUserByUserId(securityUser.getId());

		UserInfoDto userInfoDto = new UserInfoDto(user, "login");

		return ResponseEntity.ok(userInfoDto);
	}

	@PutMapping("/modify")
	@Operation(summary = "유저 정보 수정")
	@ApiResponse(responseCode = "200", description = "유저정보 수정, 토큰이 변경된 유저정보로 재발급됨", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class))})
	public ResponseEntity<?> modifyUserInfo(
		@AuthenticationPrincipal SecurityUser securityUser,
		HttpServletResponse response,
		@RequestBody UserModifyDto modifyDto
	) {
		log.info("uuid : " + securityUser.getUsername());
		UserEntity user = userService.findUserByUserId(securityUser.getId());

		user = userService.modifyUserInfo(
			user, modifyDto.getNickname(), modifyDto.getProfileImg(), modifyDto.getThumbnail(),
			modifyDto.getMemo());

		userService.createAndSetTokens(user.getId(), user.getUuid(), user.getNickname(),
			user.getAuthorities(), response);

		return ResponseEntity.ok(new UserInfoDto(user, "modify"));
	}

	@PutMapping("/register")
	@Operation(summary = "회원가입시 정보 기입")
	@ApiResponse(responseCode = "200", description = "UserInfo의 type이 register일 때 요청, 토큰이 변경된 유저정보로 재발급됨", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class))})
	public ResponseEntity<?> registerUserInfo(
		@AuthenticationPrincipal SecurityUser securityUser,
		HttpServletResponse response,
		@RequestBody UserRegisterDto registerDto
	) {
		log.info("uuid : " + securityUser.getUsername());

		UserEntity user = userService.registerUserInfo(
			securityUser.getId(), registerDto.getNickname(),
			registerDto.getProfileImg(),
			registerDto.getThumbnail(),
			registerDto.getMemo(),
			registerDto.isMarketing());

		userService.createAndSetTokens(user.getId(), user.getUuid(), user.getNickname(),
			user.getAuthorities(), response);

		return ResponseEntity.ok(new UserInfoDto(user, "modify"));
	}

	@PutMapping("/update/fcmToken")
	@Operation(summary = "fcmToken 업데이트")
	@ApiResponse(responseCode = "200", description = "fcmToken업데이트", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))})
	public ResponseEntity<?> updateFcmToken(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam String fcmToken
	) {
		int updated = userService.updateFcmTokenByUserId(securityUser.getId(), fcmToken);

		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/delete/account")
	@Operation(summary = "회원 탈퇴")
	@ApiResponse(responseCode = "200", description = "회원 탈퇴, 토큰을 삭제해야함", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))})
	public ResponseEntity<?> deleteAccount(
		@AuthenticationPrincipal SecurityUser securityUser,
		HttpServletResponse response
	) {
		UserEntity user = userService.validateUser(securityUser);
		awsAuthService.deleteImagesByUserId(securityUser.getId());
		tripService.deleteAllTripMember(securityUser.getId());
		userService.deleteUserByUser(user);
		userService.setTokenInCookie("", "", response);
		return ResponseEntity.ok("deleted");
	}
}
