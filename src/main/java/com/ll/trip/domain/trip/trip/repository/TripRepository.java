package com.ll.trip.domain.trip.trip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ll.trip.domain.trip.trip.dto.TripInfoDto;
import com.ll.trip.domain.trip.trip.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
	boolean existsById(long id);

	boolean existsByInvitationCode(String invitationCode);

	@Query("""
		SELECT new com.ll.trip.domain.trip.trip.dto.TripInfoDto(
		t.id,
		    t.name,
		    t.type,
		    t.startDate,
		    t.endDate,
		    t.country,
		    t.thumbnail,
		    t.invitationCode
		)
		FROM TripMember tm
		left join  Trip t
		WHERE tm.user.id = :userId
		""")
	List<TripInfoDto> findAllTripInfoDtosByUserId(@Param("userId") Long userId, Sort sort);

	Optional<Trip> findByInvitationCode(String invitationCode);
}
