package com.ll.trip.domain.plan.room.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class TripMemberId implements Serializable {
	private Long userId;
	private Long tripId;

	@Override
	public int hashCode() {
		return Objects.hash(userId, tripId);
	}
}
