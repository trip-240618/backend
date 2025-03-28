# backend
Spring Boot Project

프로젝트 개요

이 프로젝트는 Spring Boot 3.3.0을 기반으로 한 웹 애플리케이션입니다. MySQL을 사용하여 데이터를 관리하고, JWT 기반 인증 및 OAuth2 로그인을 지원합니다. 또한, RESTful API 문서를 위해 Spring REST Docs를 활용하며, QueryDSL을 사용한 효율적인 데이터 조회를 구현합니다.

기술 스택

언어: Java 17

프레임워크: Spring Boot 3.3.0

빌드 도구: Gradle

데이터베이스: MySQL

보안: Spring Security, JWT, OAuth2

API 문서: Spring REST Docs, Swagger

비동기 처리: Reactor Core, WebFlux

웹소켓: Spring WebSocket

클라우드 서비스: AWS S3, Firebase Admin SDK

항공 데이터 API: Amadeus API

프로젝트 설정

1. 환경 설정

프로젝트를 실행하기 위해 Java 17 및 Gradle이 필요합니다.

2. 의존성 관리

이 프로젝트는 Gradle을 사용하여 의존성을 관리합니다.

plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.0'
    id 'io.spring.dependency-management' version '1.1.5'
    id 'org.asciidoctor.jvm.convert' version '3.3.2'
}

3. 주요 라이브러리

필수 라이브러리

spring-boot-starter-web : Spring MVC 기반 웹 애플리케이션 개발

spring-boot-starter-data-jpa : JPA 사용을 위한 기본 설정 제공

spring-boot-starter-security : 보안 및 인증 기능 제공

spring-boot-starter-validation : 입력값 검증

mysql-connector-j : MySQL 데이터베이스 연결

querydsl-jpa : QueryDSL을 이용한 동적 쿼리 작성

spring-boot-starter-webflux : WebFlux를 이용한 비동기 처리

spring-boot-starter-websocket : WebSocket 지원

추가 라이브러리

jjwt : JWT 기반 인증 구현

spring-restdocs-mockmvc : RESTful API 문서 자동 생성

springdoc-openapi-starter-webmvc-ui : Swagger UI 지원

aws-java-sdk-s3 : AWS S3 파일 업로드 및 관리

firebase-admin : Firebase Admin SDK를 통한 푸시 알림 및 인증 기능

amadeus-java : Amadeus API 연동을 위한 라이브러리

실행 방법

프로젝트 클론

git clone https://github.com/your-repository.git
cd your-repository

애플리케이션 실행

./gradlew bootRun

테스트

테스트는 JUnit과 Spring Boot Test를 활용하여 수행됩니다.

./gradlew test

API 문서 생성

REST Docs를 사용하여 API 문서를 생성하려면 다음 명령을 실행하세요.

./gradlew asciidoctor

배포

이 프로젝트는 GitHub Actions를 사용하여 CI/CD를 수행하며, AWS EC2 환경에서 실행됩니다.

문의

프로젝트 관련 문의는 이메일로 주세요.
