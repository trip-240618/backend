package com.ll.trip.domain.history.history.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryReplyDto {
	private long id;

	private String writerUuid;

	private LocalDateTime createDate;

	private String content;
}
