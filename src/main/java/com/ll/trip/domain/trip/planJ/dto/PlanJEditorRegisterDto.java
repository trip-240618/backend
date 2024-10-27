package com.ll.trip.domain.trip.planJ.dto;

import lombok.Data;

@Data
public class PlanJEditorRegisterDto {
	private int day;
	private String editorUuid;
	private String nickname;

	public PlanJEditorRegisterDto(int day, String editorUuid, String nickname) {
		this.day = day;
		this.editorUuid = editorUuid;
		this.nickname = nickname;
	}
}
