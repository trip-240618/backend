package com.ll.trip.domain.trip.history.dto;

import java.math.BigDecimal;
import java.util.List;

import com.ll.trip.domain.trip.history.entity.History;
import com.ll.trip.domain.trip.history.entity.HistoryTag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HistoryDetailDto {
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
		description = "화질 좋은 사진",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String imageUrl;

	@Schema(description = "위도",
		example = "37.4220541")
	private BigDecimal latitude; //위도

	@Schema(description = "경도",
		example = "-122.08532419999999")
	private BigDecimal longitude; //경도

	private String memo;

	private List<HistoryReplyDto> replyDtos;

	private int likeCnt;

	@Schema(
		description = "태그 리스트",
		example = "[\"tag1\", \"tag2\", \"tag3\"]"
	)
	private List<String> tags;

	public HistoryDetailDto(History history) {
		this.id = history.getId();
		this.writerUuid = history.getUser().getUuid();
		this.profileImage = history.getUser().getThumbnail();
		this.imageUrl = history.getImageUrl();
		this.latitude = history.getLatitude();
		this.longitude = history.getLongitude();
		this.memo = history.getMemo();
		this.likeCnt = history.getLikeCnt();
		this.replyDtos = history.getHistoryReplies().stream().map(HistoryReplyDto::new).toList();
		this.tags = history.getHistoryTags().stream().map(HistoryTag::getTag_name).toList();
	}

}
