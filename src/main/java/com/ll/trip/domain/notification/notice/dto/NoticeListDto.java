package com.ll.trip.domain.notification.notice.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListDto {
	@Schema(description = "공지 유형", example = "업데이트")
	private String type;

	@Schema(description = "공지 제목", example = "v3.25.10 업데이트 안내")
	private String title;

	@Schema(
		description = "생성 날짜",
		example = "2024-08-22T14:05")
	private LocalDateTime createDate;
}
