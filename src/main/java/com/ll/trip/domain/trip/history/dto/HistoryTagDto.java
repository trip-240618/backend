package com.ll.trip.domain.trip.history.dto;

import com.ll.trip.domain.trip.history.entity.HistoryTag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryTagDto {
	@Schema(
		description = "기록 태그 색상",
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
