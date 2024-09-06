package com.ll.trip.domain.trip.planP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPInfoDto {
	@Schema(
		description = "plan pk",
		example = "1")
	private Long planId;

	@Schema(
		description = "시작일 기준 몇일째인지",
		example = "1")
	private Integer dayAfterStart;

	@Schema(
		description = "예정일 별 순서",
		example = "2")
	private Integer orderByDate;

	@Schema(
		description = "작성자 uuid",
		example = "c9f30d9e-0bac-4a81-b005-6a79ba4fbef4")
	private String writerUuid;

	@Schema(
		description = "플랜 내용",
		example = "호텔 체크인 하기")
	@NotBlank
	private String content;

	@Schema(
		description = "체크박스",
		example = "true")
	private boolean checkbox;


}
