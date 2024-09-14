package com.ll.trip.domain.trip.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.trip.dto.TripMemberDto;
import com.ll.trip.domain.trip.trip.dto.TripMemberServiceDto;
import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.domain.trip.trip.entity.TripMemberId;

import lombok.NonNull;

public interface TripMemberRepository extends JpaRepository<TripMember, TripMemberId> {
	boolean existsById(@NonNull TripMemberId tripMemberId);

	boolean existsTripMemberByTripIdAndUserId(long tripId, long userId);

	@Query("""
		     select new com.ll.trip.domain.trip.trip.dto.TripMemberDto(u.uuid, u.nickname,u.thumbnail, u.profileImg, tm.isLeader)
		     from TripMember tm
		     left join tm.user u
		     where tm.trip.id = :tripId
		""")
	List<TripMemberDto> findTripMemberUserByTripId(long tripId);

	@Query("""
		SELECT new com.ll.trip.domain.trip.trip.dto.TripMemberServiceDto(
		tm.trip.id,
		tm.user.uuid,
		tm.user.nickname,
		tm.user.thumbnail,
		tm.user.profileImg,
		tm.isLeader)
		FROM TripMember tm
		WHERE tm.trip.id IN :tripIds
		""")

	List<TripMemberServiceDto> findAllTripMemberDtosByTripIds(List<Long> tripIds);

}
