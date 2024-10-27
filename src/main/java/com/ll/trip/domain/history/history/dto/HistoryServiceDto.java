package com.ll.trip.domain.history.history.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistoryServiceDto {
	private long id;

	private String writerUuid;

	private String profileImage;

	private String imageUrl;

	private String thumbnail;

	private BigDecimal latitude; //위도

	private BigDecimal longitude; //경도

	private String memo;

	private int likeCnt;

	private int replyCnt;

	private boolean like;

	private LocalDate photoDate;

	private HistoryTagDto tag;


	public HistoryServiceDto(long id, String writerUuid, String profileImage, String imageUrl, String thumbnail,
		BigDecimal latitude, BigDecimal longitude, String memo, int likeCnt, int replyCnt, boolean like,
		LocalDate photoDate, Long tagId, String tagColor,
		String tagName) {
		this.id = id;
		this.writerUuid = writerUuid;
		this.profileImage = profileImage;
		this.imageUrl = imageUrl;
		this.thumbnail = thumbnail;
		this.latitude = latitude;
		this.longitude = longitude;
		this.memo = memo;
		this.likeCnt = likeCnt;
		this.replyCnt = replyCnt;
		this.like = like;
		this.photoDate = photoDate;
		if (tagId != null)
			this.tag = new HistoryTagDto(tagId, tagColor, tagName);
	}
}
