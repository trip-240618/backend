package com.ll.trip.domain.trip.planP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPInfoDto {
	@Schema(
		description = "plan pk",
		example = "1")
	private Long planId;

	@Schema(
		description = "시작일 기준 몇일째인지",
		example = "1")
	private int dayAfterStart;

	@Schema(
		description = "예정일 별 순서",
		example = "2")
	private Integer orderByDate;

	@Schema(
		description = "플랜 내용",
		example = "호텔 체크인 하기")
	@NotBlank
	private String content;

	@Schema(
		description = "체크박스",
		example = "true")
	private boolean checkbox;

	@Schema(
		description = "보관함 여부",
		example = "false")
	private boolean locker;
}
