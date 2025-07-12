package com.ll.trip.global.aws.cloudfront.dto;

import lombok.Getter;

@Getter
public class BookContentResponse {
    private final String baseUrl;

    public BookContentResponse(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}