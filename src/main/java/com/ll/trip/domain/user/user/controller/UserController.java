package com.ll.trip.domain.user.user.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.user.user.dto.UserInfoDto;
import com.ll.trip.domain.user.user.dto.UserRegisterDto;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.service.UserService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/user")
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<UserEntity> register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
		UserEntity user = userService.register(userRegisterDto);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}

	@GetMapping("/info")
	public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal SecurityUser securityUser) {
		log.info(securityUser.getUsername());
		Optional<UserEntity> user = userService.findUserByUuid(securityUser.getUuid());

		if (user.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		UserInfoDto userInfoDto = new UserInfoDto(user.get());

		return ResponseEntity.ok(userInfoDto);
	}

	@GetMapping("/loginSuccess")
	public String loginSuccess() {
		// 로그인 성공 후 처리 로직
		return "loginSuccess";
	}

	@GetMapping("/loginFailure")
	public String loginFailure() {
		// 로그인 실패 후 처리 로직
		return "loginFailure";
	}

}
