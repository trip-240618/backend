package com.ll.trip.domain.trip.trip.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ll.trip.domain.trip.trip.dto.TripCreateDto;
import com.ll.trip.domain.trip.trip.dto.TripInfoDto;
import com.ll.trip.domain.trip.trip.dto.TripMemberDto;
import com.ll.trip.domain.trip.trip.dto.TripMemberServiceDto;
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

		if (tripMemberRepository.existsById(tripMemberId)) {
			return;
		}

		TripMember tripMember = TripMember.builder()
			.id(tripMemberId)
			.isLeader(isLeader)
			.build();

		tripMemberRepository.save(tripMember);
	}

	public Trip findByInvitationCode(String invitationCode) {
		return tripRepository.findByInvitationCode(invitationCode).orElseThrow(NullPointerException::new);
	}

	public List<TripMemberDto> findTripMemberUserByTripId(Long tripId) {
		return tripMemberRepository.findTripMemberUserByTripId(tripId);
	}

	public boolean existTripMemberByTripIdAndUserId(long tripId, long userId) {
		return tripMemberRepository.existsTripMemberByTripIdAndUserId(tripId, userId);
	}

	public List<TripInfoDto> findAllByUserId(Long userId, String sortDirection, String sortField) {
		Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
		List<TripInfoDto> tripInfoDtoList = tripRepository.findAllTripInfoDtosByUserId(userId, sort);

		fillTripMemberToTripInfo(tripInfoDtoList);

		return tripInfoDtoList;
	}

	private void fillTripMemberToTripInfo(List<TripInfoDto> tripInfoDtoList) {
		Map<Long, TripInfoDto> tripMap = new HashMap<>();
		List<Long> idList = new ArrayList<>();

		for (TripInfoDto tripInfoDto : tripInfoDtoList) {
			long tripId = tripInfoDto.getId();
			idList.add(tripId);
			tripMap.put(tripId, tripInfoDto);
		}

		List<TripMemberServiceDto> tripMemberServiceDtoList = tripMemberRepository
			.findAllTripMemberDtosByTripIds(idList);

		for (TripMemberServiceDto tripMemberServiceDto : tripMemberServiceDtoList) {
			long id = tripMemberServiceDto.getId();

			TripMemberDto tripMemberDto = new TripMemberDto(
				tripMemberServiceDto.getNickname(),
				tripMemberServiceDto.getProfileImg(),
				tripMemberServiceDto.isLeader()
			);

			tripMap.get(id).getTripMemberDtoList().add(tripMemberDto);
		}
	}
}
