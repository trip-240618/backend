package com.ll.trip.global.aws.cloudfront;

import com.amazonaws.services.cloudfront.CloudFrontCookieSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.ll.trip.global.aws.cloudfront.dto.BookContentResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Date;

import static com.amazonaws.services.cloudfront.CloudFrontCookieSigner.getCookiesForCustomPolicy;

@Configuration
@ConfigurationProperties(prefix = "cloudfront")
@Getter
@Setter
public class CloudFrontSignedCookieService {
    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Value("${cloud.aws.cloudfront.secret-path}")
    private String privateKeyLocation;

    @Value("${cloud.aws.cloudfront.public-key-id}")
    private String publicKeyId;

    public BookContentResponse getBookContentResponse(
            HttpServletRequest req,
            HttpServletResponse res,
            String bookContentId
    ) throws InvalidKeySpecException, IOException {
        // 쿠키 만료 시간 설정 (60분 뒤)
        Calendar expireCalendar = Calendar.getInstance();
        expireCalendar.add(Calendar.MINUTE, 60);
        Date expireTime = expireCalendar.getTime();

        String resourcePath = bookContentId + "/*";
        File privateKeyFile = new File(privateKeyLocation);

        // 서명된 쿠키 생성
        CloudFrontCookieSigner.CookiesForCustomPolicy cookies = getCookiesForCustomPolicy(
                SignerUtils.Protocol.https,
                cloudFrontDomain,
                privateKeyFile,
                resourcePath,
                publicKeyId,
                expireTime,
                null, // 시작 시간 (null이면 즉시 사용 가능)
                null  // 허용 IP (null이면 모두 허용)
        );

        // 리소스 경로 URL 생성
        String url = SignerUtils.generateResourcePath(SignerUtils.Protocol.https, cloudFrontDomain, resourcePath);

        // 쿠키 응답에 추가
        res.addCookie(makeSignedCookie(cookies.getPolicy().getKey(), cookies.getPolicy().getValue()));
        res.addCookie(makeSignedCookie(cookies.getSignature().getKey(), cookies.getSignature().getValue()));
        res.addCookie(makeSignedCookie(cookies.getKeyPairId().getKey(), cookies.getKeyPairId().getValue()));

        // URL 마지막의 "/*" 제거
        String baseUrl = url.replaceAll("/\\*$", "");

        return new BookContentResponse(baseUrl);
    }

    private Cookie makeSignedCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setDomain(getRootDomain(cloudFrontDomain));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }

    private String getRootDomain(String domain) {
        String[] parts = domain.split("\\.");
        if (parts.length >= 2) {
            return parts[parts.length - 2] + "." + parts[parts.length - 1];
        }
        return domain;
    }

}
