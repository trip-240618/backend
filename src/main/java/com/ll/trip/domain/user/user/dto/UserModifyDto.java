package com.ll.trip.domain.user.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserModifyDto {
	@Schema(
		description = "닉네임",
		example = "master Choi")
	private String nickname;

	@Schema(
		description = "짧은 자기 소개",
		example = "매일 강해지는 아침")
	private String memo;

	@Schema(
		description = "축소된 프로필 url",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String thumbnail;

	@Schema(
		description = "presignedUrl로 등록한 프로필 url",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String profileImg;
}
