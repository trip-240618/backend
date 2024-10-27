package com.ll.trip.domain.file.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteImageDto {
	private String imageUrl;
	private String thumbnail;

	public DeleteImageDto(String imageUrl) {
		this.imageUrl = imageUrl;
		this.thumbnail = null;
	}
}
