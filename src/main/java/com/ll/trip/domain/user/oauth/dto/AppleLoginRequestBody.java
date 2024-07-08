package com.ll.trip.domain.user.oauth.dto;

import lombok.Getter;

@Getter
public class AppleLoginRequestBody {
	String email;
	String familyName;
	String givenName;
	String identityToken;
	String state;
	String userIdentifier;
}
