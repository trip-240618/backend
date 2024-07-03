package com.ll.trip.domain.user.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SocialLoginDto {

	private String providerTypeCode;
	private String profileImageUrl;
	private String providerId;
	private String nickname;
}
