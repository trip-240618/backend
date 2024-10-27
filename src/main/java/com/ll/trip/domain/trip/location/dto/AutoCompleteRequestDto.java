package com.ll.trip.domain.trip.location.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoCompleteRequestDto {
	@Schema(
		description = "장소명",
		example = "디즈니")
	private String input;

	@Schema(
		description = "IETF 및 BCP-47 언어 코드",
		example = "ko")
	private String languageCode;

	@Schema(
		description = "지역코드 ccTLD('최상위 도메인') 2자(영문 기준) 값",
		example = "[\"kr\",\"jp\"]")
	private List<String> includedRegionCodes;
}
