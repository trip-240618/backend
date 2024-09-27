package com.ll.trip.domain.trip.trip.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ll.trip.domain.trip.trip.entity.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long>{

	boolean existsByInvitationCode(String invitationCode);

	@Query("""
		    SELECT DISTINCT t
		    FROM Trip t
		    JOIN fetch t.tripMembers tm
		    LEFT JOIN fetch tm.user u
		    WHERE t.id = :tripId
		""")
	Optional<Trip> findTripDetailById(long tripId);

	@Query("""
		    SELECT distinct t
		    FROM Trip t
		    JOIN fetch t.tripMembers tm
		    join fetch tm.user u
		    WHERE t IN (SELECT tm.trip FROM TripMember tm WHERE tm.user.id = :userId and tm.trip.endDate >= :date)
		""")
	List<Trip> findTripIncommingByUserIdAndDate(Long userId, LocalDate date);

	@Query("""
		    SELECT distinct t
		    FROM Trip t
		    JOIN fetch t.tripMembers tm
		    join fetch tm.user u
		    WHERE t IN (SELECT tm.trip FROM TripMember tm WHERE tm.user.id = :userId and tm.trip.endDate < :date)
		""")
	List<Trip> findTripLastByUserIdAndDate(Long userId, LocalDate date);

	@Query("""
		    SELECT distinct t
		    FROM Trip t
		    join fetch t.tripMembers tm
		    join fetch tm.user u
		    WHERE t IN (SELECT b.trip FROM Bookmark b WHERE b.user.id = :userId)
		""")
	List<Trip> findAllBookmarkTrip(Long userId);

	@Query("""
			select distinct t
			from Trip t
			join fetch History h
			where t.id = :tripId
		""")
	List<Trip> findTripAndHistoryByTripId(long tripId);

	@Query("""
		select t.id
		from Trip t
		where t.invitationCode = :invitationCode
		""")
	long findTrip_idByInvitationCode(String invitationCode);

}
