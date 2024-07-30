package com.ll.trip.domain.file.file.dto;

import java.util.List;

import lombok.Data;

@Data
public class DeleteObjectByUrlRequestBody {
	private Boolean isUploaded;
	private List<String> urls;
}
