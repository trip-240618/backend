package com.ll.trip.domain.history.history.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HistoryListServiceDto {
	private long id;

	private String writerUuid;

	private String profileImage;

	private String imageUrl;

	private String thumbnail;

	private BigDecimal latitude; //위도

	private BigDecimal longitude; //경도

	private LocalDate photoDate;

	private String memo;

	private int likeCnt;

	private int replyCnt;

	private Long tagId;

	private String tagColor;

	private String tagName;
}
