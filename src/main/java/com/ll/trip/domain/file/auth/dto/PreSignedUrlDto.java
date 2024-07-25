package com.ll.trip.domain.file.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PreSignedUrlDto {
	private String preSignedUrl;
}

