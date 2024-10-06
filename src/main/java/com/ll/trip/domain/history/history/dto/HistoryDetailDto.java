package com.ll.trip.domain.history.history.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
		description = "작성자 프로필 축소버전",
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

	@Schema(description = "메모",
		example = "검암역을 기억해")
	private String memo;

	@Schema(description = "좋아요 수",
		example = "1")
	private int likeCnt;

	@Schema(description = "댓글 수",
		example = "3")
	private int replyCnt;

	@Schema(description = "좋아요 여부",
		example = "false")
	private boolean like;

	@Schema(description = "사진 날짜",
		example = "2024-08-22")
	private LocalDate photoDate;

	private List<HistoryTagDto> tags = new ArrayList<>();

	public HistoryDetailDto(HistoryServiceDto dto) {
		this.id = dto.getId();
		this.writerUuid = dto.getWriterUuid();
		this.profileImage = dto.getProfileImage();
		this.imageUrl = dto.getImageUrl();
		this.latitude = dto.getLatitude();
		this.longitude = dto.getLongitude();
		this.memo = dto.getMemo();
		this.likeCnt = dto.getLikeCnt();
		this.replyCnt = dto.getReplyCnt();
		this.like = dto.isLike();
		this.tags.add(dto.getTag());
	}
}
