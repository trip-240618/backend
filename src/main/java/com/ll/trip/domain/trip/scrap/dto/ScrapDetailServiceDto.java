package com.ll.trip.domain.trip.scrap.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScrapDetailServiceDto {
	private long id;

	private String writerUuid;

	private String nickname;

	private String title;

	private String content;

	private boolean hasImage;

	private String color;

	private boolean bookmark;

	private LocalDateTime createDate;

	private long imageId;

	private String imageKey;
}
