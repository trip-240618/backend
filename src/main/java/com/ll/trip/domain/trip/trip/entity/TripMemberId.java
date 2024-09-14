package com.ll.trip.domain.trip.trip.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
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
	@NotNull
	private Long userId;
	@NotNull
	private Long tripId;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TripMemberId that = (TripMemberId) o;
		return Objects.equals(this.tripId, that.getTripId()) &&
			   Objects.equals(this.userId, that.getUserId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, tripId);
	}
}
