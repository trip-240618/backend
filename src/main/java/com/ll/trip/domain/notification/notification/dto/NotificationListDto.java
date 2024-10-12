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

	@Schema(description = "알림이 발생한 Trip의 id (이동하는 url에 필요함)", example = "1")
	private Long tripId;

	@Schema(description = "알림이 발생한 Trip의 type (이동하는 url에 필요할 수 있음)", example = "j")
	private Character tripType;

	@NotBlank
	@Schema(description = "알림을 확인하는데 필요한 테이블 이름", example = "trip")
	private String type;

	@Schema(description = "보게될 데이터의 id (이동이 필요한 경우에만 포함됨)", example = "15")
	private Long typeId;

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
