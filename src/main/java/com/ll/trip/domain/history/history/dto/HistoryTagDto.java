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
		description = "히스토리 태그 컬러",
		example = "#FFEFF3")
	private String tagColor;

	@Schema(
		description = "태그명",
		example = "#라멘")
	private String tagName;

	public HistoryTagDto(HistoryTag tag) {
		this.tagColor = tag.getTagColor();
		this.tagName = tag.getTagName();
	}
}
