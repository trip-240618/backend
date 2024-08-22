package com.ll.trip.domain.trip.trip.service;

import org.springframework.stereotype.Service;

import com.ll.trip.domain.trip.trip.dto.TripCreateDto;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.domain.trip.trip.entity.TripMemberId;
import com.ll.trip.domain.trip.trip.repository.TripMemberRepository;
import com.ll.trip.domain.trip.trip.repository.TripRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripService {

	private final TripRepository tripRepository;
	private final TripMemberRepository tripMemberRepository;
	private final InvitationCodeGenerator invitationCodeGenerator;

	public Long createTrip(TripCreateDto tripCreateDto, String invitationCode) {
		Trip trip = Trip.builder()
			.invitationCode(invitationCode)
			.name(tripCreateDto.getName())
			.type(tripCreateDto.getType())
			.startDate(tripCreateDto.getStartDate())
			.endDate(tripCreateDto.getEndDate())
			.country(tripCreateDto.getCountry())
			.thumbnail(tripCreateDto.getThumbnail())
			.build();

		trip = tripRepository.save(trip);

		return trip.getId();
	}

	public String generateInvitationCode() {
		return invitationCodeGenerator.generateUniqueCode();
	}

	public void joinTripById(Long tripId, Long userId, boolean isLeader) {
		TripMemberId tripMemberId = TripMemberId.builder()
			.tripId(tripId)
			.userId(userId)
			.build();

		if(tripMemberRepository.existsById(tripMemberId)) {
			return;
		}

		TripMember tripMember = TripMember.builder()
			.id(tripMemberId)
			.isLeader(isLeader)
			.build();

		tripMemberRepository.save(tripMember);
	}
}
