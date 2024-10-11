package com.ll.trip.domain.mypage.faq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FaqCreateDto {
	@Schema(description = "질문 내용", example = "여행 일정은 최대 몇개까지 추가 가능한가요?")
	@NotBlank
	private String title;

	@Schema(description = "질문 유형", example = "여행 일정")
	@NotBlank
	private String type;

	@Schema(description = "답변", example = "여행 일정")
	@Column(length = 1500)
	@NotBlank
	private String content;
}
