package com.ll.trip.domain.file.file.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreSignedUrlResponseDto {
	private List<String> preSignedUrls;
}

