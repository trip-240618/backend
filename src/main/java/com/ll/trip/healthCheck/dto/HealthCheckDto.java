package com.ll.trip.healthCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HealthCheckDto {
	String serverName;
	String serverAddress;
	String serverPort;
	String env;
}
