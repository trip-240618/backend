package com.ll.trip.domain.trip.planJ.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanJCreateRequestDto {
	@Schema(
		description = "시작일 기준 몇일째인지",
		example = "1")
	private int dayAfterStart;

	@Schema(
		description = "일정 시작 시간",
		example = "14:30")
	private LocalTime startTime;

	@NotBlank
	@Schema(
		description = "플랜 내용",
		example = "호텔 체크인 하기")
	private String title;

	@Schema(
		description = "일정 장소",
		example = "도쿄 디즈니")
	private String place;

	@Schema(
		description = "메모",
		example = "3시 이후에 체크인 가능")
	private String memo;

	@Schema(description = "위도",
		example = "37.4220541")
	private BigDecimal latitude;

	@Schema(description = "경도",
		example = "-122.08532419999999")
	private BigDecimal longitude;

	@Schema(description = "보관함 여부",
		example = "false")
	private boolean locker;
}
