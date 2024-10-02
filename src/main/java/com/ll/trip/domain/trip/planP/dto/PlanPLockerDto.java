package com.ll.trip.domain.trip.planP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPLockerDto {
	@Schema(
		description = "plan pk",
		example = "1")
	private long planId;

	@Schema(
		description = "이동 후 day, 보관함으로 간다면 null",
		example = "2")
	private Integer dayTo;

	@Schema(
		description = "요청시에는 null, 응답에는 값이 담겨 있음",
		example = "22")
	private Integer order;

	@Schema(
		description = "수정된 보관함 여부",
		example = "false")
	private boolean locker;
}
