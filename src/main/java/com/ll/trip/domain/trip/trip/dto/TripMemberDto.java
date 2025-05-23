package com.ll.trip.domain.trip.trip.dto;

import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.domain.user.user.entity.UserEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripMemberDto {
	@Schema(
		description = "uuid",
		example = "c9f30d9e-0bac-4a81-b005-6a79ba4fbef4")
	private String uuid;

	@Schema(
		description = "닉네임",
		example = "master Choi")
	private String nickname;

	@Schema(
		description = "축소된 프로필 url",
		example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
	private String thumbnail;

	@Schema(
		description = "방장여부",
		example = "true")
	private boolean isLeader;

	public TripMemberDto(TripMember tripMember) {
		UserEntity user = tripMember.getUser();
		this.uuid = user.getUuid();
		this.nickname = user.getNickname();
		this.thumbnail = user.getThumbnail();
		this.isLeader = tripMember.isLeader();
	}
}
