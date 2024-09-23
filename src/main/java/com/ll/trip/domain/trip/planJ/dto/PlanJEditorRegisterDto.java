package com.ll.trip.domain.trip.planJ.dto;

import lombok.Data;

@Data
public class PlanJEditorRegisterDto {
	private int day;
	private String editorUuid;

	public PlanJEditorRegisterDto(int day, String editorUuid) {
		this.day = day;
		this.editorUuid = editorUuid;
	}
}
