package com.ll.trip.global.security.filter.cloudfront;

import com.amazonaws.services.cloudfront.CloudFrontCookieSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.amazonaws.services.cloudfront.CloudFrontCookieSigner.getCookiesForCustomPolicy;

@Component
@RequiredArgsConstructor
@Slf4j
public class CloudFrontSignedCookieUtil {
    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Value("${cloud.aws.cloudfront.secret-path}")
    private String privateKeyLocation;

    @Value("${cloud.aws.cloudfront.public-key-id}")
    private String publicKeyId;

    public void setCookie(
            HttpServletResponse res
    ) throws InvalidKeySpecException, IOException {
        // 쿠키 만료 시간 설정 (60분 뒤)
        Calendar expireCalendar = Calendar.getInstance();
        expireCalendar.add(Calendar.MINUTE, 60);
        Date expireTime = expireCalendar.getTime();

        File privateKeyFile = new ClassPathResource(privateKeyLocation).getFile();

        // 서명된 쿠키 생성
        CloudFrontCookieSigner.CookiesForCustomPolicy cookies = getCookiesForCustomPolicy(
                SignerUtils.Protocol.https,
                cloudFrontDomain,
                privateKeyFile,
                "/*",
                publicKeyId,
                expireTime,
                null, // 시작 시간 (null이면 즉시 사용 가능)
                null  // 허용 IP (null이면 모두 허용)
        );

        // 쿠키 응답에 추가
        res.addCookie(makeSignedCookie(cookies.getPolicy().getKey(), cookies.getPolicy().getValue()));
        res.addCookie(makeSignedCookie(cookies.getSignature().getKey(), cookies.getSignature().getValue()));
        res.addCookie(makeSignedCookie(cookies.getKeyPairId().getKey(), cookies.getKeyPairId().getValue()));
    }

    public Cookie makeSignedCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setDomain(getRootDomain(cloudFrontDomain));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }

    public String getRootDomain(String domain) {
        String[] parts = domain.split("/");
        if (parts.length >= 3) {
            return parts[2];
        }
        return domain;
    }

    public Map<String, String> extractCloudFrontCookies(Cookie[] cookies) {
        Map<String, String> map = new HashMap<>();
        if (cookies == null) return map;
        for (Cookie c : cookies) {
            if (c.getName().contains("CloudFront-")) {
                map.put(c.getName(), c.getValue());
            }
        }
        return map;
    }

    public boolean isCookieExpired(String policyBase64) {
        try {
            String decoded = new String(Base64.getDecoder().decode(policyBase64), StandardCharsets.UTF_8);
            // JSON 형태의 policy에서 "DateLessThan" > "AWS:EpochTime" 추출
            long expireTime = extractEpochTime(decoded);
            return System.currentTimeMillis() / 1000 > expireTime;
        } catch (Exception e) {
            return true; // 파싱 실패 시 만료된 것으로 간주
        }
    }

    public long extractEpochTime(String json) {
        Pattern pattern = Pattern.compile("\"DateLessThan\"\\s*:\\s*\\{[^}]*\"AWS:EpochTime\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        throw new IllegalArgumentException("Invalid policy format");
    }
}
