package com.ll.trip.domain.user.user.dto;

import com.ll.trip.domain.user.user.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
	private Long id;
	private String name;
	private String profileImg;

	public static UserInfoDto from(UserEntity user) {
		return new UserInfoDto(
			user.getId(),
			user.getName(),
			user.getProfileImg()
		);
	}

	public UserEntity toEntity() {
		return UserEntity.builder()
			.name(this.name)
			.profileImg(this.profileImg)
			.build();
	}
}
