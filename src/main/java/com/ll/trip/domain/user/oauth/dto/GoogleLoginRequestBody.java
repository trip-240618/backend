package com.ll.trip.domain.user.oauth.dto;

import lombok.Data;

@Data
public class GoogleLoginRequestBody {
	private String displayName;
	private String email;
	private String id;
	private String photoUrl;
	private String serverAuthCode;
	private String fcmToken;
}
