package com.ll.trip.domain.trip.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.domain.trip.trip.entity.TripMemberId;

public interface TripMemberRepository extends JpaRepository<TripMember, TripMemberId> {
	boolean existsById(TripMemberId tripMemberId);
}
