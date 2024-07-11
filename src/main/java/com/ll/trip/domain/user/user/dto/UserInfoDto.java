package com.ll.trip.domain.user.user.dto;

import com.ll.trip.domain.user.user.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
	private String name;
	private String profileImg;
	private String type;

	public UserInfoDto(UserEntity user, String type) {
		this.name = user.getName();
		this.profileImg = user.getProfileImg();
		this.type = type;
	}
}
