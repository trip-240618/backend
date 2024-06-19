package com.ll.trip.healthCheck.controller;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<?> healthCheck() {
		//무중단 배포를 위해 해당 포트의 서버가 켜져있는지 확인
		Map<String, String> responseData = new TreeMap<>();
		responseData.put("serverName",serverName);
		responseData.put("serverAddress",serverAddress);
		responseData.put("serverPort",serverPort);
		responseData.put("env",env);
		return ResponseEntity.ok(responseData);
	}

	@GetMapping("/env")
	public ResponseEntity<?> getEnv() {
		return ResponseEntity.ok(env);
	}
}
