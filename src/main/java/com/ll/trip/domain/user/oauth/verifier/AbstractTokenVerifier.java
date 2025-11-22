package com.ll.trip.domain.user.oauth.verifier;

import com.ll.trip.domain.user.oauth.dto.SocialUserInfo;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.text.ParseException;
import java.util.Date;

@Slf4j
public abstract class AbstractTokenVerifier implements TokenVerifier {
    protected abstract String getJwkUrl();
    protected abstract String getIssuer();
    protected abstract String getClientId();

    @Override
    public boolean verify(String idToken) {
        try {
            // JWK 키셋 로드
            URL jwkUrl = new URL(getJwkUrl());
            JWKSet jwkSet = JWKSet.load(jwkUrl);

            // JWT 파싱
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWK jwk = jwkSet.getKeyByKeyId(signedJWT.getHeader().getKeyID());

            if (jwk == null) {
                throw new RuntimeException("No matching JWK found.");
            }

            // 서명 검증
            RSASSAVerifier verifier = new RSASSAVerifier(jwk.toRSAKey());
            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Signature verification failed.");
            }

            // Claims 검증
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            if (!claims.getIssuer().equals(getIssuer())) {
                throw new RuntimeException("Invalid issuer.");
            }

            if (!claims.getAudience().contains(getClientId())) {
                log.info("clientId : " + getClientId());
                throw new RuntimeException("Invalid audience.");
            }

            if (claims.getExpirationTime().before(new Date())) {
                throw new RuntimeException("Token expired.");
            }

            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public SocialUserInfo extractUserInfo(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // 1. JWT 클레임에서 정보 추출 (클레임이 없을 경우 null)
            String socialId = claims.getSubject(); // sub (항상 존재)
            String email = claims.getStringClaim("email");
            String picture = claims.getStringClaim("picture");
            String name = claims.getStringClaim("name");

            return new SocialUserInfo(socialId, email, picture, name);

        } catch (ParseException e) {
            // ID Token이 JWT 형식으로 파싱되지 못했을 때 발생하는 예외
            throw new RuntimeException("ID Token 파싱 실패", e);
        }
    }
}
