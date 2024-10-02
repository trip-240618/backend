package com.ll.trip.domain.trip.trip.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TripModifyDto {
	@Schema(
		description = "여행방 이름",
		example = "일주일 도쿄 투어")
	private String name;

	@Schema(
		description = "여행방 썸네일",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String thumbnail;

	@Schema(
		description = "여행방 라벨 컬러",
		example = "#FFEFF3")
	private String labelColor;

	@Schema(
		description = "여행 시작일",
		example = "2024-08-22")
	private LocalDate startDate;

	@Schema(
		description = "여행 종료일",
		example = "2024-08-29")
	private LocalDate endDate;
}
