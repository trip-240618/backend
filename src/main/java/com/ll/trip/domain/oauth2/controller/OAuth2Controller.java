package com.ll.trip.domain.oauth2.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2Controller {
    // application.yml에서 lltrip://login/success 값을 주입받습니다.
    @Value("${oauth2.redirect.app-scheme}")
    private String appSchemeRedirectUrl;

    /**
     * Google 콘솔에 등록된 HTTPS 엔드포인트입니다.
     * 이 엔드포인트는 JWT 토큰을 쿠키로 받은 후,
     * Spring MVC의 'redirect:' 접두사를 사용하여 즉시 앱 스킴(lltrip://)으로 최종 리다이렉트 시킵니다.
     * * @return "redirect:lltrip://login/success" 형태의 문자열을 반환하여 302 리다이렉트 수행
     */
    @GetMapping("/login/success")
    // @ResponseBody // ✅ 불필요한 HTML 반환을 막고 302 리다이렉션을 사용하기 위해 제거
    public String appRedirect() {

        // Spring MVC에게 'appSchemeRedirectUrl' 주소로 302 리다이렉트하라고 지시합니다.
        // 웹뷰는 이 302 응답을 받은 후, 'lltrip://...' 주소로 이동을 시도하고 Flutter가 이를 인터셉트합니다.
        return "redirect:" + appSchemeRedirectUrl;
    }
}
