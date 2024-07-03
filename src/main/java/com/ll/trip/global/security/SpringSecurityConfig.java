package com.ll.feelko.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/member/mypage/**","/payment/**","/chat/**")
                                //예약,결제도 추가
                                .authenticated()
                                .anyRequest()
                                .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession() // 세션 고정 공격 방지
                        .invalidSessionUrl("/member/auth-result")
                        .maximumSessions(1).expiredUrl("/member/auth-result?failMsg="+URLEncoder.encode("비정상적인 접근입니다.", StandardCharsets.UTF_8))
                )
                .csrf(
                        csrf ->
                                csrf.ignoringRequestMatchers(
                                        "**", //모든 post요청에 csrf토큰 심고나서 삭제
                                        "/oauth2/**" //소셜 로그인시 토큰과 사용자정보 받을 수 있도록
                                )
                )
                .headers(
                        headers ->
                                headers
                                        .frameOptions(
                                                HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                )
                )
                .formLogin(
                        (formLogin) ->
                                formLogin
                                        .usernameParameter("email") // email 필드 이름을 지정
                                        .passwordParameter("password") // password 필드 이름을 지정
                                        .loginPage("/member/login")
                                        .defaultSuccessUrl("/member/auth-result?msg=" + URLEncoder.encode("환영합니다.", StandardCharsets.UTF_8))
                                        .failureUrl("/member/login?failMsg=" + URLEncoder.encode("아이디 또는 비밀번호가 일치하지 않습니다.", StandardCharsets.UTF_8))
                )
                .oauth2Login(
                        oauth2Login -> oauth2Login
                                .loginPage("/member/login")
                                .defaultSuccessUrl("/member/oauth2-result?msg=" + URLEncoder.encode("환영합니다.", StandardCharsets.UTF_8))
                                .failureUrl("/member/login?failMsg=" + URLEncoder.encode("로그인에 실패했습니다.", StandardCharsets.UTF_8))
                )
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true))
        ;

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
