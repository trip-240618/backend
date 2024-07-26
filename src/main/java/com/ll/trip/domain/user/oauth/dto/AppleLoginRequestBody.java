package com.ll.trip.domain.user.oauth.dto;

import lombok.Data;

@Data
public class AppleLoginRequestBody {
	private String email;
	private String familyName;
	private String givenName;
	private String identityToken;
	private String state;
	private String userIdentifier;
}
