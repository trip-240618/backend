package com.ll.trip.domain.file.file.dto;

import lombok.Data;

@Data
public class PreSignedUrlRequestBody {
	private String prefix;
	private String fileName;
}
