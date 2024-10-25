package com.ll.trip.domain.trip.trip.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TripInfoServiceDto {
	private long id;

	private String name;

	private char type;

	private LocalDate startDate;

	private LocalDate endDate;

	private String country;

	private String regionCode;

	private String thumbnail;

	private String invitationCode;

	private String labelColor;

	private boolean bookmark;

	TripMemberDto tripMemberDto;

	public TripInfoServiceDto(long id, String name, char type, LocalDate startDate, LocalDate endDate, String country, String regionCode,
		String thumbnail, String invitationCode, String labelColor, boolean bookmark, String uuid, String nickname, String memberThumbnail, boolean isLeader) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.country = country;
		this.regionCode = regionCode;
		this.thumbnail = thumbnail;
		this.invitationCode = invitationCode;
		this.labelColor = labelColor;
		this.bookmark = bookmark;
		this.tripMemberDto = new TripMemberDto(uuid, nickname, memberThumbnail, isLeader);
	}
}
