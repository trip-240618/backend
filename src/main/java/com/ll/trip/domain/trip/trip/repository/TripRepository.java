package com.ll.trip.domain.trip.trip.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ll.trip.domain.trip.trip.dto.TripImageDeleteDto;
import com.ll.trip.domain.trip.trip.dto.TripInfoServiceDto;
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
		    SELECT new com.ll.trip.domain.trip.trip.dto.TripInfoServiceDto(t.id, t.name, t.type, t.startDate, t.endDate, t.country,
		    t.thumbnail, t.invitationCode, t.labelColor, COALESCE(b.toggle, false), u.uuid, u.nickname, u.thumbnail, tm.isLeader)
		    FROM TripMember tm
		    inner join tm.trip t on tm.user.id =: userId and t.endDate >= :date and t.id = tm.trip.id
		    left join t.bookmarks b on b.trip.id = t.id and b.user.id = :userId
		    left join tm.user u on u.id = tm.trip.id
		""")
	List<TripInfoServiceDto> findTripIncommingByUserIdAndDate(Long userId, LocalDate date);

	@Query("""
		    SELECT new com.ll.trip.domain.trip.trip.dto.TripInfoServiceDto(t.id, t.name, t.type, t.startDate, t.endDate, t.country,
		    t.thumbnail, t.invitationCode, t.labelColor, COALESCE(b.toggle, false), u.uuid, u.nickname, u.thumbnail, tm.isLeader)
		    FROM TripMember tm
		    inner join tm.trip t on tm.user.id =: userId and t.endDate < :date and t.id = tm.trip.id
		    left join t.bookmarks b on b.trip.id = t.id and b.user.id = :userId
		    left join tm.user u on u.id = tm.trip.id
		""")
	List<TripInfoServiceDto> findTripLastByUserIdAndDate(Long userId, LocalDate date);

	@Query("""
			SELECT new com.ll.trip.domain.trip.trip.dto.TripInfoServiceDto(t.id, t.name, t.type, t.startDate, t.endDate, t.country,
			t.thumbnail, t.invitationCode, t.labelColor, b.toggle, u.uuid, u.nickname, u.thumbnail, tm.isLeader)
			FROM Bookmark b
			inner join b.trip t on b.user.id =: userId and b.toggle = true and t.id = b.trip.id
			left join TripMember tm on tm.trip.id = t.id
			left join tm.user u on u.id = tm.trip.id
		""")
	List<TripInfoServiceDto> findAllBookmarkTrip(Long userId);

	@Query("""
			select new com.ll.trip.domain.trip.trip.dto.TripImageDeleteDto(t.thumbnail, h.thumbnail, h.imageUrl)
			from Trip t
			left join History h on h.trip.id = t.id
			where t.id = :tripId
		""")
	List<TripImageDeleteDto> findTripAndHistoryByTripId(long tripId);

	@Query("""
		select t.id
		from Trip t
		where t.invitationCode = :invitationCode
		""")
	long findTrip_idByInvitationCode(String invitationCode);

}
