package com.ll.trip.domain.trip.planJ.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

import com.ll.trip.domain.trip.planJ.entity.PlanJ;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanJInfoDto {
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
		description = "일정 시작 시간",
		example = "14:30")
	private LocalTime startTime;

	@Schema(
		description = "작성자 uuid",
		example = "c9f30d9e-0bac-4a81-b005-6a79ba4fbef4")
	private String writerUuid;

	@NotBlank
	@Schema(
		description = "플랜 내용",
		example = "호텔 체크인 하기")
	private String title;

	@Schema(
		description = "메모",
		example = "3시 이후에 체크인 가능"
				  + "항공편의 경우 jsonString 항공편 정보")
	private String memo;

	@Schema(
		description = "일정 장소",
		example = "도쿄 디즈니")
	private String place;

	@Schema(description = "위도",
		example = "37.4220541")
	private BigDecimal latitude;

	@Schema(description = "경도",
		example = "-122.08532419999999")
	private BigDecimal longitude;

	@Schema(description = "보관함 여부",
		example = "false")
	private boolean locker;

	public PlanJInfoDto(PlanJ plan) {
		this.planId = plan.getId();
		this.dayAfterStart = plan.getDayAfterStart();
		this.orderByDate = plan.getOrderByDate();
		this.startTime = plan.getStartTime();
		this.writerUuid = plan.getWriterUuid();
		this.title = plan.getTitle();
		this.memo = plan.getMemo();
		this.latitude = plan.getLatitude();
		this.longitude = plan.getLongitude();
		this.locker = plan.isLocker();
	}
}
