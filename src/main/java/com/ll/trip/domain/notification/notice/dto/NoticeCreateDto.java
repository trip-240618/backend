package com.ll.trip.domain.notification.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoticeCreateDto {
	@Schema(description = "공지 유형", example = "업데이트")
	@NotBlank
	private String type;

	@Schema(description = "공지 제목", example = "v3.25.10 업데이트 안내")
	@NotBlank
	private String title;

	@Schema(description = "공지 내용", example = "안녕하세요, 트립스토리입니다....")
	@NotBlank
	private String content;

	@Schema(description = "공지 상세 (마크다운)", example = "")
	private String markdownDetails;
}
