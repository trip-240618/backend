package com.ll.trip.domain.trip.history.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoriesCreateRequestDto {
	private List<HistoryCreateRequestDto> historyCreateRequestDtos;
}
