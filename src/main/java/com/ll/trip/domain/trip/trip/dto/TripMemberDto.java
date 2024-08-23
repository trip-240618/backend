package com.ll.trip.domain.trip.trip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripMemberDto {
	@Schema(
		description = "닉네임",
		example = "master Choi")
	private String nickname;

	@Schema(
		description = "프로필 url",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String profileImg;

	@Schema(
		description = "방장여부",
		example = "true")
	private boolean isLeader;
}
