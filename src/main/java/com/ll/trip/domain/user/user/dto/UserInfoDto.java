package com.ll.trip.domain.user.user.dto;

import com.ll.trip.domain.user.user.entity.UserEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
	@Schema(
		description = "실제이름",
		example = "최순자")
	private String name;

	@Schema(
		description = "닉네임",
		example = "master Choi")
	private String nickName;

	@Schema(
		description = "presignedUrl로 등록한 프로필 url",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String profileImg;

	@Schema(
		description = "유저 데이터의 상태 또는 기능명",
		example = "register or login or modify ...")
	private String type;

	public UserInfoDto(UserEntity user, String type) {
		this.name = user.getName();
		this.nickName = user.getNickname();
		this.profileImg = user.getProfileImg();
		this.type = type;
	}
}
