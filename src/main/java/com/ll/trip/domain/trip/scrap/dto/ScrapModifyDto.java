package com.ll.trip.domain.trip.scrap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrapModifyDto {
	@Schema(
		description = "스크랩 pk",
		example = "1")
	private long id;

	@NotBlank
	@Schema(
		description = "스크랩 제목",
		example = "예수님이 입은 옷이 작을 때 뭐라하게")
	private String title;

	@Lob
	@NotBlank
	@Schema(
		description = "스크랩 내용",
		example = "십..자가..")
	private String content;

	@Schema(
		description = "사진포함 여부",
		example = "false")
	private boolean hasImage;

	@Schema(
		description = "스크랩 컬러",
		example = "#FFEFF3")
	private String color;

	@Schema(
		description = "수정x 응답에 북마크 조인시키기 싫어서 만듬",
		example = "true")
	private boolean bookmark;
}
