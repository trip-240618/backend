package com.ll.trip.domain.trip.location.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TextSearchDto {
	@NotBlank
	private String textQuery;
}
