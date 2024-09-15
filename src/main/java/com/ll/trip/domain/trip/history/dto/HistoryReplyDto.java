package com.ll.trip.domain.trip.history.dto;

import java.time.LocalDateTime;

import com.ll.trip.domain.trip.history.entity.HistoryReply;

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

	public HistoryReplyDto(HistoryReply reply) {
		this.id = reply.getId();
		this.writerUuid = reply.getWriterUuid();
		this.createDate = reply.getCreateDate();
		this.content = reply.getContent();
	}
}
