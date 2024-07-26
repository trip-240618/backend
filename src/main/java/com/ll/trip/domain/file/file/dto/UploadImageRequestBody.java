package com.ll.trip.domain.file.file.dto;

import java.util.List;

import lombok.Data;

@Data
public class UploadImageRequestBody {
	private List<String> imgUrls;
}
