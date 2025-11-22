package com.ll.trip.domain.user.oauth.verifier;

import org.springframework.stereotype.Component;

@Component
public class AppleTokenVerifier extends AbstractTokenVerifier {
    @Override
    protected String getJwkUrl() {
        return "https://appleid.apple.com/auth/keys";
    }

    @Override
    protected String getIssuer() {
        return "https://appleid.apple.com";
    }

    @Override
    protected String getClientId() {
        //apple client Id
        return "(자신의 Client Id 값)";
    }
}
