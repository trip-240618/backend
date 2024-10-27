package com.ll.trip.domain.notification.notification.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationListDto {
	@Schema(description = "알림 id", example = "1")
	private long id;

	@Schema(description = "라벨 색상", example = "FFEFF3")
	private String labelColor;

	@NotBlank
	@Schema(description = "알림 내용으로 이동하는 url")
	private String destination;

	@NotBlank
	@Schema(description = "알림의 제목", example = "여행 일정")
	private String title;

	@NotBlank
	@Schema(description = "알림의 내용", example = "'...'방에 새 일정이 추가되었습니다.")
	private String content;

	@Schema(description = "기독 여부", example = "false")
	private boolean isRead;

	@Schema(
		description = "생성 날짜",
		example = "2024-08-22T14:05")
	private LocalDateTime createDate;
}
