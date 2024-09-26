package com.ll.trip.domain.trip.trip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ll.trip.domain.trip.trip.entity.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long>, TripRepositoryCustom {

	boolean existsByInvitationCode(String invitationCode);

	Optional<Trip> findByInvitationCode(String invitationCode);

	@Query("""
		select t.id
		from Trip t
		where t.invitationCode = :invitationCode
		""")
	Optional<Long> getTripIdByInvitationCode(String invitationCode);
}
