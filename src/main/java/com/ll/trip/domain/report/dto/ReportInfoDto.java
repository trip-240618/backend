package com.ll.trip.domain.report.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.ll.trip.domain.history.history.dto.HistoryDto;
import com.ll.trip.domain.history.history.dto.HistoryReplyDto;
import com.ll.trip.domain.trip.scrap.dto.ScrapDetailDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReportInfoDto {

	private long id;

	private String type; //history, reply, scrap

	private long typeId;

	@Schema(oneOf = {HistoryReplyDto.class, HistoryDto.class, ScrapDetailDto.class})
	private String jsonData;

	private String deleteUrl;

	private boolean complete;

	@CreatedDate
	private LocalDateTime createDate;
}
