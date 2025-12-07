package com.ll.trip.domain.user.oauth.dto;

public record SocialUserInfo(String socialId,      // 고유 식별자 (Subject)
                             String email,         // 이메일
                             String profileImageUrl, // 프로필 사진 URL
                             String name          // 이름 (선택적)
) {
}
