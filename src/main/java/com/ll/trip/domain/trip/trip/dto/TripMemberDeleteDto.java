package com.ll.trip.domain.trip.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TripMemberDeleteDto {
	private long tripId;
	private boolean isLeader;
	private int memberCnt;
}
