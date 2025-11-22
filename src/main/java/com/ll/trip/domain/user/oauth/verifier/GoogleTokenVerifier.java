package com.ll.trip.domain.user.oauth.verifier;

public class GoogleTokenVerifier extends AbstractTokenVerifier{
    @Override
    protected String getJwkUrl() {
        return "https://www.googleapis.com/oauth2/v3/certs";
    }

    @Override
    protected String getIssuer() {
        return "https://accounts.google.com";
    }

    @Override
    protected String getClientId() {
        //google clientId 프론트와 동일해야함
        return "726664801128-b1tei1nn8smaatd88s9g0clhpr8if9fa.apps.googleusercontent.com";
    }
}
