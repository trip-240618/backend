package com.ll.trip.domain.history.history.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDayDto {
	@Schema(
		description = "사진 날짜",
		example = "2024-08-22")
	private LocalDate photoDate;

	private List<HistoryDto> historyList;

	public HistoryDayDto(LocalDate photoDate) {
		this.photoDate = photoDate;
		this.historyList = new ArrayList<>();
	}
}
