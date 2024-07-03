package com.ll.trip.domain.user.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ll.trip.domain.user.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	private final UserService userService;

	@Value("${server.serverAddress}")
	private String serverAddress;

	@GetMapping("/login/kakao")
	public String kakaoLogin() {
		return "redirect:http://" + serverAddress + "/oauth2/authorization/kakao";
	}
}
