package com.ll.trip.domain.trip.scrap.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrapListDto {
	@Schema(
		description = "스크랩의 pk",
		example = "1")
	private long id;

	@Schema(
		description = "uuid",
		example = "c9f30d9e-0bac-4a81-b005-6a79ba4fbef4")
	private String writerUuid;

	@Schema(
		description = "작성자 닉네임",
		example = "작성자")
	private String nickname;

	@NotBlank
	@Schema(
		description = "스크랩 제목",
		example = "예수님이 입은 옷이 작을 때 뭐라하게")
	private String title;

	@NotBlank
	@Schema(
		description = "스크랩 미리보기 내용",
		example = "십..자가..")
	private String preview;

	@Schema(
		description = "사진포함 여부",
		example = "false")
	private boolean hasImage;

	@Schema(
		description = "스크랩 컬러",
		example = "#FFEFF3")
	private String color;

	@Schema(
		description = "북마크 여부",
		example = "true")
	private boolean bookmark;

	@Schema(
		description = "생성 날짜",
		example = "2024-08-22T14:05")
	private LocalDateTime createDate;
}
