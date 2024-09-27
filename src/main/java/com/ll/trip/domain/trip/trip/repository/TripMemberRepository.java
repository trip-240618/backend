package com.ll.trip.domain.trip.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.domain.trip.trip.entity.TripMemberId;
import com.ll.trip.domain.user.user.entity.UserEntity;

import lombok.NonNull;

public interface TripMemberRepository extends JpaRepository<TripMember, TripMemberId> {
	boolean existsById(@NonNull TripMemberId tripMemberId);

	int countByUser(UserEntity user);

	boolean existsTripMemberByTripIdAndUserId(long tripId, long userId);

	@Query("""
		select tm.isLeader
		from TripMember tm
		where tm.user.id = :userId
		and tm.trip.id = :tripId
		""")
	boolean isLeaderOfTrip(long userId, long tripId);
}
