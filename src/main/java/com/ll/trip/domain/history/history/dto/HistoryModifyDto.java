package com.ll.trip.domain.history.history.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HistoryModifyDto {
	@Schema(description = "메모", example = "오사카에서 찍은 사진")
	private String memo;

	private List<HistoryTagDto> tags = new ArrayList<>();
}
