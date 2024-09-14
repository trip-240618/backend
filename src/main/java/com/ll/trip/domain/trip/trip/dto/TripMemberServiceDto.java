package com.ll.trip.domain.trip.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripMemberServiceDto {
	private long id;

	private String uuid;

	private String nickname;

	private String thumbnail;

	private String profileImg;

	private boolean isLeader;
}
