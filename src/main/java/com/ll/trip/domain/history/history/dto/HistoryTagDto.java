package com.ll.trip.domain.history.history.dto;

import com.ll.trip.domain.history.history.entity.HistoryTag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryTagDto {
	@Schema(
		description = "히스토리 태그 id",
		example = "1")
	private Long id;

	@Schema(
		description = "히스토리 태그 컬러",
		example = "FFEFF3")
	private String tagColor;

	@Schema(
		description = "태그명",
		example = "#라멘")
	private String tagName;

	public HistoryTagDto(HistoryTag tag) {
		this.id = tag.getId();
		this.tagColor = tag.getTagColor();
		this.tagName = tag.getTagName();
	}

	public HistoryTagDto(String tagColor, String tagName) {
		this.tagColor = tagColor;
		this.tagName = tagName;
	}
}
