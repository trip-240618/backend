package com.ll.trip.domain.history.history.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ll.trip.domain.history.history.entity.History;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HistoryListDto {
	@Schema(
		description = "history의 pk",
		example = "12")
	private long id;

	@Schema(
		description = "작성자 uuid",
		example = "c9f30d9e-0bac-4a81-b005-6a79ba4fbef4")
	private String writerUuid;

	@Schema(
		description = "프로필 축소버전",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String profileImage;

	@NotBlank
	@Schema(
		description = "축소된 사진",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String thumbnail;

	@Schema(description = "위도",
		example = "37.4220541")
	private BigDecimal latitude; //위도

	@Schema(description = "경도",
		example = "-122.08532419999999")
	private BigDecimal longitude; //경도

	@Schema(
		description = "사진 날짜",
		example = "2024-08-22T14:05")
	private LocalDateTime photoDate;

	@Schema(
		description = "태그 리스트",
		example = "[\"tag1\", \"tag2\", \"tag3\"]"
	)
	private List<HistoryTagDto> tags;

	public HistoryListDto(History history) {
		this.id = history.getId();
		this.writerUuid = history.getUser().getUuid();
		this.profileImage = history.getUser().getThumbnail();
		this.thumbnail = history.getThumbnail();
		this.latitude = history.getLatitude();
		this.longitude = history.getLongitude();
		this.photoDate = history.getPhotoDate();
		this.tags = history.getHistoryTags().stream().map(HistoryTagDto::new).toList();
	}
}
