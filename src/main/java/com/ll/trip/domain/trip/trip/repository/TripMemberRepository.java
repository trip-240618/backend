package com.ll.trip.domain.trip.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.trip.dto.TripMemberDeleteDto;
import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.domain.trip.trip.entity.TripMemberId;

import lombok.NonNull;

public interface TripMemberRepository extends JpaRepository<TripMember, TripMemberId> {
	boolean existsById(@NonNull TripMemberId tripMemberId);

	boolean existsTripMemberByTripIdAndUserId(long tripId, long userId);

	@Query("""
		select tm.isLeader
		from TripMember tm
		where tm.user.id = :userId
		and tm.trip.id = :tripId
		""")
	boolean isLeaderOfTrip(long userId, long tripId);

	@Modifying
	@Query("""
		from TripMember tm
		left join tm.user u on tm.trip.id = :tripId and u.uuid = :uuid
		""")
	void deleteByTripIdAndUuid(long tripId, String uuid);

	@Modifying
	@Query(value = """
		UPDATE TripMember tm
		SET tm.isLeader = 1
		WHERE tm.id = (SELECT MIN(tm1.id) FROM TripMember tm1 WHERE tm1.trip.id = :tripId)
		""")
	void handLeaderToMember(long tripId);

	@Query("""
				select new com.ll.trip.domain.trip.trip.dto.TripMemberDeleteDto(
				tm.trip.id, tm.isLeader, SIZE(tm.trip.tripMembers))
		from TripMember tm
		where tm.trip.id = :tripId
		""")
	TripMemberDeleteDto findDeleteDtoBy(long tripId);

	@Query("""
				select new com.ll.trip.domain.trip.trip.dto.TripMemberDeleteDto(
				tm.trip.id, tm.isLeader, SIZE(tm.trip.tripMembers))
		from TripMember tm
		where tm.user.id = :userId
		""")
	List<TripMemberDeleteDto> findAllDeleteDtoBy(long userId);
}
