package com.ll.trip.healthCheck.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HealthCheckDto {
	String serverName;
	String serverAddress;
	String serverPort;
	String env;
}
