package com.ll.trip.domain.trip.trip.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ll.trip.domain.trip.trip.dto.TripImageDeleteDto;
import com.ll.trip.domain.trip.trip.dto.TripInfoServiceDto;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.user.user.dto.VisitedCountryDto;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

	boolean existsByInvitationCode(String invitationCode);

	@Query("""
		    SELECT t
		    FROM Trip t
		    JOIN fetch t.tripMembers tm
		    LEFT JOIN fetch tm.user u
		    WHERE t.id = :tripId
		""")
	Optional<Trip> findTripDetailById(long tripId);

	@Query("""
		    SELECT new com.ll.trip.domain.trip.trip.dto.TripInfoServiceDto(t.id, t.name, t.type, t.startDate, t.endDate, t.country,
		    t.regionCode, t.thumbnail, t.invitationCode, t.labelColor, COALESCE(b.toggle, false), u.uuid, u.nickname, u.thumbnail, tm2.isLeader)
		    FROM TripMember tm
		    inner join tm.trip t on tm.user.id = :userId and t.endDate >= :date
		    left join t.bookmarks b on b.user.id = :userId
		    left join t.tripMembers tm2
		    left join tm2.user u
		""")
	List<TripInfoServiceDto> findTripIncomingByUserIdAndDate(Long userId, LocalDate date);

	@Query("""
		    SELECT new com.ll.trip.domain.trip.trip.dto.TripInfoServiceDto(t.id, t.name, t.type, t.startDate, t.endDate, t.country,
		    t.regionCode, t.thumbnail, t.invitationCode, t.labelColor, COALESCE(b.toggle, false), u.uuid, u.nickname, u.thumbnail, tm2.isLeader)
		    FROM TripMember tm
		    inner join tm.trip t on tm.user.id = :userId and t.endDate < :date
		    left join t.bookmarks b on b.user.id = :userId
		    left join t.tripMembers tm2
		    left join tm2.user u
		""")
	List<TripInfoServiceDto> findTripLastByUserIdAndDate(Long userId, LocalDate date);

	@Query("""
			SELECT new com.ll.trip.domain.trip.trip.dto.TripInfoServiceDto(t.id, t.name, t.type, t.startDate, t.endDate, t.country,
			t.regionCode, t.thumbnail, t.invitationCode, t.labelColor, b.toggle, u.uuid, u.nickname, u.thumbnail, tm.isLeader)
			FROM Bookmark b
			inner join b.trip t on b.user.id = :userId and b.toggle = true
			left join t.tripMembers tm
			left join tm.user u on u.id = tm.trip.id
		""")
	List<TripInfoServiceDto> findAllBookmarkTrip(long userId);

	@Query("""
			select new com.ll.trip.domain.trip.trip.dto.TripImageDeleteDto(t.thumbnail, h.thumbnail, h.imageUrl)
			from Trip t
			inner join t.histories h on t.id = :tripId
		""")
	List<TripImageDeleteDto> findTripAndHistoryByTripId(long tripId);

	@Query("""
		select t.id
		from Trip t
		where t.invitationCode = :invitationCode
		""")
	Optional<Long> findTrip_idByInvitationCode(String invitationCode);

	@Query("""
		 select new com.ll.trip.domain.user.user.dto.VisitedCountryDto(t.country, count(t.id))
		 from TripMember tm
		 inner join tm.trip t on tm.user.id = :userId and t.startDate <= :date
		 group by t.country
		""")
	List<VisitedCountryDto> findVisitedCountry(long userId, LocalDate date);

	@Modifying
	@Query("""
		    UPDATE Trip t
		    SET t.name = :name,
		        t.thumbnail = :thumbnail,
		        t.labelColor = :labelColor
		    WHERE t.id = :tripId
		""")
	void updateTripById(long tripId, String name, String thumbnail, String labelColor);
}
