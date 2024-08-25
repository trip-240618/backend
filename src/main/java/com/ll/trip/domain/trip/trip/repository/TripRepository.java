package com.ll.trip.domain.trip.trip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ll.trip.domain.trip.trip.dto.TripInfoDto;
import com.ll.trip.domain.trip.trip.entity.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long>, TripRepositoryCustom {

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
		    t.invitationCode,
		  	t.labelColor
		)
		FROM Bookmark b
		left join  Trip t
		WHERE b.user.id = :userId
		""")
	List<TripInfoDto> findAllBookmarkTripInfoDtosByUserId(@Param("userId") Long userId);

	Optional<Trip> findByInvitationCode(String invitationCode);
}
