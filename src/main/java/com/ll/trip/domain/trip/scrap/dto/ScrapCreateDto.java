package com.ll.trip.domain.trip.scrap.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrapCreateDto {
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

	private List<String>
}
