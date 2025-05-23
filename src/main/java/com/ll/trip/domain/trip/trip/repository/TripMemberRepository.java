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
	@Query("""
		delete from TripMember tm
		where tm.trip.id = :tripId and tm.user.id = :userId
		""")
	void deleteByTripIdAndUserId(long tripId, long userId);

	@Modifying
	@Query(value = """
		UPDATE trip_member tm
        JOIN (
            SELECT MIN(tm1.user_id) AS min_user_id
            FROM trip_member tm1
            WHERE tm1.trip_id = :tripId
        ) AS subquery
        ON tm.user_id = subquery.min_user_id and tm.trip_id = :tripId
        SET tm.is_leader = 1
    """, nativeQuery = true)
	void handLeaderToMember(long tripId);

	@Query("""
				select new com.ll.trip.domain.trip.trip.dto.TripMemberDeleteDto(
				tm.trip.id, tm.isLeader, SIZE(tm.trip.tripMembers))
		from TripMember tm
		where tm.trip.id = :tripId
		and tm.user.id = :userId
		""")
	TripMemberDeleteDto findDeleteDtoBy(long tripId, long userId);

	@Query("""
				select new com.ll.trip.domain.trip.trip.dto.TripMemberDeleteDto(
				tm.trip.id, tm.isLeader, SIZE(tm.trip.tripMembers))
		from TripMember tm
		where tm.user.id = :userId
		""")
	List<TripMemberDeleteDto> findAllDeleteDtoBy(long userId);
}
