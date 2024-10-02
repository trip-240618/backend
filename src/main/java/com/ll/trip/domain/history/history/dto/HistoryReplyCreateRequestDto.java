package com.ll.trip.domain.history.history.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HistoryReplyCreateRequestDto {
	@Schema(
		description = "댓글 내용",
		example = "메로나")
	private String content;
}
