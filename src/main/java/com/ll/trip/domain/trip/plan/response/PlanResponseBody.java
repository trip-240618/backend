package com.ll.trip.domain.trip.plan.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PlanResponseBody<T> {
	@Schema(description = "실행될 명령어", example = "swap")
	private String command;

	@Schema(description = "send()를 통해 전송한 Data")
	private T data;

	public PlanResponseBody(String command, T data) {
		this.command = command;
		this.data = data;
	}
}
