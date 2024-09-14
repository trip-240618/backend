package com.ll.trip.domain.trip.trip.dto;

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
public class TripCreateDto {
	@Schema(
		description = "여행방 이름",
		example = "일주일 도쿄 투어")
	@NotBlank
	private String name;

	@Schema(
		description = "여행방 타입",
		example = "J or P")
	private char type;

	@Schema(
		description = "여행 시작일",
		example = "2024-08-22")
	@NotNull
	private LocalDate startDate;

	@Schema(
		description = "여행 종료일",
		example = "2024-08-29")
	@NotNull
	private LocalDate endDate;

	@Schema(
		description = "여행지",
		example = "일본")
	@NotBlank
	private String country;

	@Schema(
		description = "여행방 썸네일(화질 축소해서)",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	@NotBlank
	private String thumbnail;

	@Schema(
		description = "여행방 라벨 색상",
		example = "#FFEFF3")
	@NotBlank
	private String labelColor;
}
