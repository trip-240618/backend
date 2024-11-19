package com.ll.trip.domain.trip.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripImageDeleteDto {
	private String tripThumbnail;
	private String historyThumbnail;
	private String historyImage;
}
