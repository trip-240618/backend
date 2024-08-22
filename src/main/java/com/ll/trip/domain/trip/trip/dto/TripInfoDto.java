package com.ll.trip.domain.trip.trip.dto;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripInfoDto {
	@Schema(
		description = "여행방 이름",
		example = "일주일 도쿄 투어")
	private String name;

	@Schema(
		description = "여행방 타입",
		example = "J or P")
	private String type;

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
		description = "여행방 썸네일",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String thumbnail;

	@Schema(
		description = "초대코드",
		example = "일본")
	private String invitationCode;

	@Schema(
		description = "참가자 리스트",
		example = "[\n\t{\n\t\t\"nickname\" : \"최순자\",\n\t\t \"profileImg\" : \"https://...]\",\n\t\t \"isLeader\" : \"true\"\n\t},\n\t {...}\n]")
	private List<TripMemberDto> tripMemberDtoList;
}
