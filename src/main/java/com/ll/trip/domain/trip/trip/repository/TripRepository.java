package com.ll.trip.domain.trip.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.trip.trip.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
	boolean existsById(long id);

	int countByInvitationCode(String invitationCode);
}
