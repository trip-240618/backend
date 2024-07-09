package com.ll.trip.healthCheck.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.healthCheck.dto.HealthCheckDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class healthCheckController {
	@Value("${server.env}")
	private String env;
	@Value("${server.port}")
	private String serverPort;
	@Value("${server.serverAddress}")
	private String serverAddress;
	@Value("${serverName}")
	private String serverName;

	@GetMapping("/hc")
	@Operation(summary = "서버 정보")
	@ApiResponse(responseCode = "200", description = "로그인시에 서버 정보 반환")
	public ResponseEntity<HealthCheckDto> healthCheck() {
		//무중단 배포를 위해 해당 포트의 서버가 켜져있는지 확인
		HealthCheckDto responseData = new HealthCheckDto(serverName, serverAddress, serverPort, env);
		return ResponseEntity.ok(responseData);
	}

	@GetMapping("/env")
	@Operation(summary = "운영중인 서버 이름")
	@ApiResponse(responseCode = "200", description = "도커에서 blue, green중 실행중인 컨테이너의 이름을 반환", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
	public ResponseEntity<?> getEnv() {
		return ResponseEntity.ok(env);
	}
}
