package com.ll.trip.domain.file.file.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreSignedUrlResponseDto {
	private List<String> preSignedUrls;
}

