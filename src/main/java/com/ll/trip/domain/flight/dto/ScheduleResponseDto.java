package com.ll.trip.domain.flight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseDto {
	@Schema(
		description = "저장된 항공편의 id (저장하기 전엔 null)",
		example = "1")
	private Long flightId;

	@Schema(
		description = "항공편명",
		example = "KE")
	private String airlineCode;

	@Schema(
		description = "항공편 번호",
		example = "101")
	private int airlineNumber;

	@Schema(
		description = "출발 시간 STD 해당 지역의 시차가 적용됨",
		example = "2024-09-16T10:15+09:00")
	private String departureDate;

	@Schema(
		description = "출발 공항코드 IATA",
		example = "ICN")
	private String departureAirport;

	@Schema(
		description = "출발 공항 한글이름",
		example = "인천 국제공항")
	private String departureAirport_kr;

	@Schema(
		description = "도착 시간 STA 해당 지역의 시차가 적용됨",
		example = "2024-09-16T11:45+08:00")
	private String arrivalDate;

	@Schema(
		description = "도착 공항코드 IATA",
		example = "NRT")
	private String arrivalAirport;

	@Schema(
		description = "도착 공항 한글이름",
		example = "나리타 국제공항")
	private String arrivalAirport_kr;
}
