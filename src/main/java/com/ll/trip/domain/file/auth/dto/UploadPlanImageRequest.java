package com.ll.trip.domain.file.auth.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UploadPlanImageRequest {
	String domain;
	Long roomId;
	Long idx;
	List<String> ImgUrls;
}
