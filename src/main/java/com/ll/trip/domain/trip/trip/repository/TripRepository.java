package com.ll.trip.domain.trip.trip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.trip.trip.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
	boolean existsById(long id);

	boolean existsByInvitationCode(String invitationCode);

	// @Query("select t.id from Trip t where t.invitationCode = :invitationCode")
	// Integer findByInvitationCode(@Param("invitationCode") String invitationCode);

	Optional<Trip> findByInvitationCode(String invitationCode);
}
