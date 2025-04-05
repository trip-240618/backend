package com.ll.trip.domain.trip.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripMemberDeleteDto {
	private long tripId;
	private boolean isLeader;
	private int memberCnt;
}
