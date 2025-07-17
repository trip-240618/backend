package com.ll.trip.global.security.filter.cloudfront;

import com.amazonaws.services.cloudfront.CloudFrontCookieSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static PrivateKey cachedPrivateKey;

    @PostConstruct
    public void initializePrivateKey() throws ServerException {
        if (cachedPrivateKey == null) { // 이미 로드되지 않은 경우에만 로드
            try (InputStream inputStream = new ClassPathResource(privateKeyLocation).getInputStream()) {
                // 1. PEM 파일 내용을 String으로 읽어옴
                String privateKeyContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                String cleanedKeyContent = privateKeyContent.replaceAll("\\s", "");
                byte[] decodedKey = Base64.getDecoder().decode(cleanedKeyContent);
                // 4. PKCS8EncodedKeySpec 생성 및 PrivateKey 로드
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                cachedPrivateKey = keyFactory.generatePrivate(spec); // 로드된 키를 캐시
                log.info("CloudFront Private Key loaded and cached successfully from: {}", privateKeyLocation);

            } catch (NoSuchAlgorithmException e) {
                // RSA 알고리즘을 찾을 수 없는 경우 (거의 발생하지 않음)
                log.error("RSA 알고리즘을 찾을 수 없습니다: {}", e.getMessage(), e);
                throw new ServerException("[CloudFront 쿠키 발급 실패] 암호화 알고리즘 오류", e);
            } catch (InvalidKeySpecException e) {
                // 키 스펙 오류 (주로 PEM 형식 문제, 즉 "algid parse error" 발생 지점)
                log.error("프라이빗 키 스펙 오류: {}", e.getMessage(), e);
                throw new ServerException("[CloudFront 쿠키 발급 실패] 키 스펙 오류: " + e.getMessage(), e);
            } catch (IOException e) {
                // 키 파일을 찾거나 읽을 수 없는 경우
                log.error("프라이빗 키 파일을 읽는 중 오류 발생 (경로: {}): {}", privateKeyLocation, e.getMessage(), e);
                throw new ServerException("[CloudFront 쿠키 발급 실패] 키 파일 읽기 오류", e);
            } catch (IllegalArgumentException e) {
                // Base64 디코딩 실패 등 유효하지 않은 인자
                log.error("프라이빗 키 내용이 유효하지 않습니다 (Base64 디코딩 또는 형식 오류): {}", e.getMessage(), e);
                throw new ServerException("[CloudFront 쿠키 발급 실패] 키 내용 형식 오류", e);
            }
        }
    }

    public void setCookie(
            HttpServletResponse res
    ) throws InvalidKeySpecException, IOException {
        // 쿠키 만료 시간 설정 (60분 뒤)
        Calendar expireCalendar = Calendar.getInstance();
        expireCalendar.add(Calendar.MINUTE, 60);
        Date expireTime = expireCalendar.getTime();

        try {
            PrivateKey privateKey = getPrivateKey(); // 캐시된 키 사용

            // 서명된 쿠키 생성
            CloudFrontCookieSigner.CookiesForCustomPolicy cookies = CloudFrontCookieSigner.getCookiesForCustomPolicy(
                    SignerUtils.Protocol.https,
                    cloudFrontDomain,
                    privateKey,
                    "/*", // CloudFront 리소스 경로 (모든 경로 허용)
                    publicKeyId,
                    expireTime,
                    null, // 시작 시간 (null이면 즉시 사용 가능)
                    null  // 허용 IP (null이면 모두 허용)
            );

            // 쿠키 응답에 추가
            res.addCookie(makeSignedCookie(cookies.getPolicy().getKey(), cookies.getPolicy().getValue()));
            res.addCookie(makeSignedCookie(cookies.getSignature().getKey(), cookies.getSignature().getValue()));
            res.addCookie(makeSignedCookie(cookies.getKeyPairId().getKey(), cookies.getKeyPairId().getValue()));
        } catch (Exception e) { // getPrivateKey()에서 던질 수 있는 IllegalStateException 및 기타 SignerUtils 오류
            log.error("CloudFront 쿠키 발급 중 예외 발생: {}", e.getMessage(), e);
            throw new ServerException("[CloudFront 쿠키 발급 실패] " + e.getMessage(), e);
        }
    }

    private PrivateKey getPrivateKey() {
        if (cachedPrivateKey == null) {
            // initializePrivateKey()가 호출되지 않았거나 실패했을 경우
            log.error("CloudFront Private Key가 초기화되지 않았습니다. 애플리케이션 시작 시 오류를 확인하세요.");
            throw new IllegalStateException("CloudFront Private Key가 초기화되지 않았습니다. 애플리케이션 시작 시 오류를 확인하세요.");
        }
        return cachedPrivateKey;
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
        int dotIndex = domain.indexOf('.');
        if (dotIndex != -1) {
            return domain.substring(dotIndex + 1);
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
            log.warn("CloudFront Policy 만료 확인 중 오류 발생 (정책 파싱 실패): {}", e.getMessage());
            return true; // 파싱 실패 시 만료된 것으로 간주
        }
    }

    private long extractEpochTime(String json) {
        Pattern pattern = Pattern.compile("\"DateLessThan\"\\s*:\\s*\\{[^}]*\"AWS:EpochTime\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        throw new IllegalArgumentException("Invalid policy format");
    }
}
