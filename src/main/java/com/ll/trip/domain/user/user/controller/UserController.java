package com.ll.trip.domain.user.user.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.user.user.dto.UserInfoDto;
import com.ll.trip.domain.user.user.dto.UserModifyDto;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.service.UserService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
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
		Optional<UserEntity> user = userService.findUserByUuid(securityUser.getUuid());

		if (user.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		UserInfoDto userInfoDto = new UserInfoDto(user.get(),"login");

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
		Optional<UserEntity> user = userService.findUserByUuid(securityUser.getUuid());

		if (user.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		UserInfoDto userInfoDto = userService.modifyUserInfo(user.get(), modifyDto);

		return ResponseEntity.ok(userInfoDto);
	}



}
