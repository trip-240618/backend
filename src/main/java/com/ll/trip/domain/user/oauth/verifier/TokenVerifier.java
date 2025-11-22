package com.ll.trip.domain.user.oauth.verifier;

import com.ll.trip.domain.user.oauth.dto.SocialUserInfo;

public interface TokenVerifier {
    boolean verify(String idToken);

    SocialUserInfo extractUserInfo(String idToken);
}
