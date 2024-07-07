package com.ll.trip.domain.user.oauth.dto;

import lombok.Getter;

@Getter
public class GoogleLoginRequestBody {
	String displayName;
	String email;
	String id;
	String photoUrl;
	String serverAuthCode;
}
