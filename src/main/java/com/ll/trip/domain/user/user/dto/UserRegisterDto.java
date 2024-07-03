package com.ll.trip.domain.user.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRegisterDto {
	@NotBlank
	private String name;

	private String providerId;

	private String password;

	private String profileImg;

	private String provider;
}
