package com.ll.trip.domain.history.history.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistoryImageDeleteDto {
	private String thumbnail;
	private String imageUrl;
}
