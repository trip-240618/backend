package com.ll.trip.domain.trip.trip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripCreateResponseDto {

	@Schema(description = "트립 id", example = "1")
	private long tripId;

	@Schema(description = "초대코드", example = "1A2B3C4D")
	private String invitationCode;
}
