package com.ll.trip.domain.notification.notice.dto;

import org.threeten.bp.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoticeListDto {
	private String type;
	private String title;
	private LocalDateTime createTime;

}
