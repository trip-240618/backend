package com.ll.trip.domain.user.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VisitedCountryDto {
	@Schema(
		description = "나라이름",
		example = "일본")
	private String country;
	@Schema(
		description = "방문 횟수",
		example = "4")
	private long visitCnt;
}
