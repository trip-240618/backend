package com.ll.trip.domain.user.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "유저정보 API")
public class UserController {

	private final UserService userService;

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

	@PostMapping("/modify")
	@Operation(summary = "유저 정보 수정")
	@ApiResponse(responseCode = "200", description = "유저정보 수정", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class))})
	public ResponseEntity<?> modifyUserInfo(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody UserModifyDto modifyDto
	) {
		log.info("uuid : " + securityUser.getUsername());
		UserEntity user = userService.findUserByUserId(securityUser.getId());

		UserInfoDto userInfoDto = userService.modifyUserInfo(
			user, modifyDto.getNickname(), modifyDto.getProfileImg(), modifyDto.getThumbnail(),
			modifyDto.getMemo());

		return ResponseEntity.ok(userInfoDto);
	}

	@PostMapping("/register")
	@Operation(summary = "유저 정보 수정")
	@ApiResponse(responseCode = "200", description = "유저정보 수정", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class))})
	public ResponseEntity<?> registerUserInfo(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody UserRegisterDto registerDto
	) {
		log.info("uuid : " + securityUser.getUsername());
		UserEntity user = userService.findUserByUserId(securityUser.getId());

		UserInfoDto userInfoDto = userService.registerUserInfo(
			user, registerDto.getNickname(),
			registerDto.getProfileImg(),
			registerDto.getThumbnail(),
			registerDto.getMemo(),
			registerDto.isMarketing());

		return ResponseEntity.ok(userInfoDto);
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

}
