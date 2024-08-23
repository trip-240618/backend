package com.ll.trip.domain.trip.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.trip.dto.TripMemberDto;
import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.domain.trip.trip.entity.TripMemberId;

public interface TripMemberRepository extends JpaRepository<TripMember, TripMemberId> {
	boolean existsById(TripMemberId tripMemberId);

	boolean existsTripMemberByTripIdAndUserId(long tripId, long userId);

	// @Query("select t.id from Trip t where t.invitationCode = :invitationCode")
	// Integer findByInvitationCode(@Param("invitationCode") String invitationCode);

	@Query("""
		     select new com.ll.trip.domain.trip.trip.dto.TripMemberDto(u.nickname, u.profileImg, tm.isLeader)
		     from TripMember tm
		     left join tm.user u
		     where tm.trip.id = :tripId
		""")
	List<TripMemberDto> findTripMemberUserByTripId(long tripId);
}
