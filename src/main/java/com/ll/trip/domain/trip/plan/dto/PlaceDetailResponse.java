package com.ll.trip.domain.trip.plan.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Google Places API에서 제공된 장소 상세 정보")
public class PlaceDetailResponse {

	@Schema(description = "장소의 포맷된 주소", example = "1600 Amphitheatre Pkwy, Mountain View, CA 94043 미국")
	private String formattedAddress;

	@Schema(description = "장소의 지리적 위치 정보")
	private Location location;

	@Schema(description = "장소의 표시 이름")
	private DisplayName displayName;

	@Data
	@Schema(description = "장소의 위도 및 경도 정보")
	public static class Location {
		@Schema(description = "위도", example = "37.4220541")
		private BigDecimal latitude;

		@Schema(description = "경도", example = "-122.08532419999999")
		private BigDecimal longitude;
	}

	@Data
	@Schema(description = "장소의 이름 정보")
	public static class DisplayName {
		@Schema(description = "장소의 이름", example = "구글플렉스")
		private String text;

		@Schema(description = "이름의 언어 코드", example = "ko")
		private String languageCode;
	}
}
