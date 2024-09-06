package com.ll.trip.domain.trip.plan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoCompleteRequestDto {
	@Schema(
		description = "나라이름 + 장소명",
		example = "일본 디즈니")
	private String input;

	@Schema(
		description = "IETF 및 BCP-47 언어 코드",
		example = "ko")
	private String languageCode;
}
