package com.ll.trip.domain.notification.notice.dto;

import java.time.LocalDateTime;

import com.ll.trip.domain.notification.notice.entity.Notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDetailDto {
	@Schema(description = "공지 id", example = "1")
	private long id;

	@Schema(description = "공지 유형", example = "업데이트")
	private String type;

	@Schema(description = "공지 제목", example = "v3.25.10 업데이트 안내")
	private String title;

	@Schema(description = "공지 내용", example = "안녕하세요, 트립스토리입니다....")
	private String content;

	@Schema(description = "일시", example = "2024년 8월 2일 (금) 04:00 ~ 06:00")
	private String duration;

	@Schema(description = "사유", example = "전산시스템 점검")
	private String reason;

	@Schema(
		description = "생성 날짜",
		example = "2024-08-22T14:05")
	private LocalDateTime createDate;

	public NoticeDetailDto(Notice notice) {
		this.id = notice.getId();
		this.type = notice.getType();
		this.title = notice.getTitle();
		this.content = notice.getContent();
		this.duration = notice.getDuration();
		this.reason = notice.getReason();
		this.createDate = notice.getCreateDate();
	}
}
