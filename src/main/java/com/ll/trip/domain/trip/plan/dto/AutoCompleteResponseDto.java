package com.ll.trip.domain.trip.plan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoCompleteResponseDto {
	@Schema(description = "자동완성 기능에서 얻은 placeId", example = "ChIJj61dQgK6j4AR4GeTYWZsKWw")
	private String placeId;
	@Schema(description = "전체 주소", example = "일본 지바현 우라야스시 마이하마 1-1 도쿄 디즈니랜드")
	private String address;
	@Schema(description = "이름을 제외한 주소", example = "일본 지바현 우라야스시 마이하마 1-1")
	private String secondaryAddress;
}
