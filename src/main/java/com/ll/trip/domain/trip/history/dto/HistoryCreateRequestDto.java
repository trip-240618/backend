package com.ll.trip.domain.trip.history.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryCreateRequestDto {
	@NotBlank
	@Schema(
		description = "축소된 사진",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String thumbnail;

	@NotBlank
	@Schema(
		description = "화질 좋은 사진",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String imageUrl;

	@Schema(description = "위도",
		example = "37.4220541")
	private BigDecimal latitude;

	@Schema(description = "경도",
		example = "-122.08532419999999")
	private BigDecimal longitude;

	@Schema(
		description = "사진 날짜",
		example = "2024-08-22T14:05")
	private LocalDateTime photoDate;

	@Schema(
		description = "메모",
		example = "오사카에서 찍은 사진")
	private String memo;

	@Schema(
		description = "태그 리스트",
		example = "[\"tag1\", \"tag2\", \"tag3\"]"
	)
	private List<HistoryTagDto> tags = new ArrayList<>();
}
