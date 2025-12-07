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
        return "959079453967-3e4sk20p2sbn2oum98bp53ed7m1f8o4t.apps.googleusercontent.com";
    }
}
