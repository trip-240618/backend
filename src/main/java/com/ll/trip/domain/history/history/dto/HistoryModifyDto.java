package com.ll.trip.domain.history.history.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HistoryModifyDto {
	@Schema(description = "메모", example = "오사카에서 찍은 사진")
	private String memo;

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

	private List<HistoryTagDto> tags = new ArrayList<>();
}
