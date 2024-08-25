package com.ll.trip.domain.trip.plan.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
		description = "예정일",
		example = "2024-08-22")
	@NotNull
	private LocalDate startDate;
}
