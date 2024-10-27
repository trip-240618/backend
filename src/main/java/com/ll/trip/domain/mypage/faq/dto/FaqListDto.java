package com.ll.trip.domain.mypage.faq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaqListDto {
	@Schema(description = "FAQ id", example = "1")
	private long id;

	@Schema(description = "질문 내용", example = "여행 일정은 최대 몇개까지 추가 가능한가요?")
	@NotBlank
	private String title;

	@Schema(description = "질문 유형", example = "여행 일정")
	@NotBlank
	private String type;
}
