package com.ll.trip.domain.trip.trip.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ll.trip.domain.trip.trip.entity.Trip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripInfoDto {
	@Schema(
		description = "여행방의 pk",
		example = "12")
	private long id;

	@Schema(
		description = "여행방 이름",
		example = "일주일 도쿄 투어")
	private String name;

	@Schema(
		description = "여행방 타입",
		example = "J or P")
	private char type;

	@Schema(
		description = "여행 시작일",
		example = "2024-08-22")
	private LocalDate startDate;

	@Schema(
		description = "여행 종료일",
		example = "2024-08-29")
	private LocalDate endDate;

	@Schema(
		description = "여행지",
		example = "일본")
	private String country;

	@Schema(
		description = "ccTLD 지역 코드",
		example = "jp")
	private String regionCode;

	@Schema(
		description = "여행방 썸네일",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String thumbnail;

	@Schema(
		description = "초대코드",
		example = "1A2B3C4D")
	private String invitationCode;

	@Schema(
		description = "여행방 라벨 컬러",
		example = "FFEFF3")
	private String labelColor;

	@Schema(
		description = "북마크 여부",
		example = "false")
	private boolean bookmark;

	@Schema(
		description = "참가자 리스트")
	private List<TripMemberDto> tripMemberDtoList = new ArrayList<>();

	public TripInfoDto(Trip trip) {
		this.id = trip.getId();
		this.name = trip.getName();
		this.type = trip.getType();
		this.invitationCode = trip.getInvitationCode();
		this.country = trip.getCountry();
		this.startDate = trip.getStartDate();
		this.endDate = trip.getEndDate();
		this.thumbnail = trip.getThumbnail();
		this.labelColor = trip.getLabelColor();
		this.bookmark = false;
		this.tripMemberDtoList = trip.getTripMembers().stream().map(TripMemberDto::new).toList();
	}

	public TripInfoDto(TripInfoServiceDto dto) {
		this.id = dto.getId();
		this.name = dto.getName();
		this.type = dto.getType();
		this.invitationCode = dto.getInvitationCode();
		this.country = dto.getCountry();
		this.regionCode = dto.getRegionCode();
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		this.thumbnail = dto.getThumbnail();
		this.labelColor = dto.getLabelColor();
		this.bookmark = dto.isBookmark();
		this.tripMemberDtoList.add(dto.getTripMemberDto());
	}
}
