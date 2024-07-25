package com.ll.trip.domain.file.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreSignedUrlRequestBody {
	private String prefix;
	private String fileName;
}
