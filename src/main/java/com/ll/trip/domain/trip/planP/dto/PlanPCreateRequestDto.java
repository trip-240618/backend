package com.ll.trip.domain.trip.planP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPCreateRequestDto {
	@Schema(
		description = "플랜 내용",
		example = "호텔 체크인 하기")
	@NotBlank
	private String content;

	@Schema(
		description = "시작일 기준 몇일째인지, 보관함일 경우 null",
		example = "1")
	private Integer dayAfterStart;

	@Schema(
		description = "보관함 여부",
		example = "false")
	private boolean locker;
}
