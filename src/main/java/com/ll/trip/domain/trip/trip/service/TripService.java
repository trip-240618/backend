package com.ll.trip.domain.trip.trip.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.trip.dto.TripCreateDto;
import com.ll.trip.domain.trip.trip.dto.TripInfoDto;
import com.ll.trip.domain.trip.trip.dto.TripInfoServiceDto;
import com.ll.trip.domain.trip.trip.dto.TripModifyDto;
import com.ll.trip.domain.trip.trip.entity.Bookmark;
import com.ll.trip.domain.trip.trip.entity.BookmarkId;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.domain.trip.trip.entity.TripMemberId;
import com.ll.trip.domain.trip.trip.repository.BookmarkRepository;
import com.ll.trip.domain.trip.trip.repository.TripMemberRepository;
import com.ll.trip.domain.trip.trip.repository.TripRepository;
import com.ll.trip.domain.user.user.dto.VisitedCountryDto;
import com.ll.trip.domain.user.user.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripService {

	private final TripRepository tripRepository;
	private final TripMemberRepository tripMemberRepository;
	private final InvitationCodeGenerator invitationCodeGenerator;

	//bookmark
	private final BookmarkRepository bookmarkRepository;

	@Transactional
	public Trip createTrip(TripCreateDto tripCreateDto, String invitationCode) {
		Trip trip = Trip.builder()
			.invitationCode(invitationCode)
			.name(tripCreateDto.getName())
			.type(tripCreateDto.getType())
			.startDate(tripCreateDto.getStartDate())
			.endDate(tripCreateDto.getEndDate())
			.country(tripCreateDto.getCountry())
			.thumbnail(tripCreateDto.getThumbnail())
			.labelColor(tripCreateDto.getLabelColor())
			.build();

		return tripRepository.save(trip);
	}

	public String generateInvitationCode() {
		return invitationCodeGenerator.generateUniqueCode();
	}

	@Transactional
	public boolean joinTripById(Trip trip, UserEntity user, boolean isLeader) {
		TripMemberId tripMemberId = TripMemberId.builder().tripId(trip.getId()).userId(user.getId()).build();

		if (tripMemberRepository.existsById(tripMemberId)) {
			return false;
		}

		TripMember tripMember = TripMember.builder().id(tripMemberId).user(user).trip(trip).isLeader(isLeader).build();

		tripMemberRepository.save(tripMember);
		return true;
	}

	public boolean existTripMemberByTripIdAndUserId(long tripId, long userId) {
		return tripMemberRepository.existsTripMemberByTripIdAndUserId(tripId, userId);
	}

	public List<TripInfoDto> findAllIncomingByUserId(Long userId, LocalDate date) {
		List<TripInfoServiceDto> serviceDtos = tripRepository.findTripIncomingByUserIdAndDate(userId, date);
		return convertToTripInfoDto(serviceDtos);
	}

	public List<TripInfoDto> findAllLastByUserIdAndDate(long userId, LocalDate date) {
		List<TripInfoServiceDto> serviceDtos = tripRepository.findTripLastByUserIdAndDate(userId, date);
		return convertToTripInfoDto(serviceDtos);
	}

	public List<TripInfoDto> findBookmarkByUserId(long userId) {
		List<TripInfoServiceDto> serviceDtos = tripRepository.findAllBookmarkTrip(userId);
		return convertToTripInfoDto(serviceDtos);
	}

	public List<TripInfoDto> convertToTripInfoDto(List<TripInfoServiceDto> serviceDtos) {
		List<TripInfoDto> tripInfoDtoList = new ArrayList<>();
		Map<Long, Integer> tripIdxMap = new HashMap<>();

		for (TripInfoServiceDto dto : serviceDtos) {
			long tripId = dto.getId();
			if (tripIdxMap.containsKey(tripId)) {
				tripInfoDtoList.get(tripIdxMap.get(tripId)).getTripMemberDtoList().add(dto.getTripMemberDto());
			} else {
				tripIdxMap.put(tripId, tripInfoDtoList.size());
				TripInfoDto tripInfoDto = new TripInfoDto(dto);
				tripInfoDtoList.add(tripInfoDto);
			}
		}

		return tripInfoDtoList;
	}

	@Transactional
	public void deleteTripById(Long id) {
		tripRepository.deleteById(id);
	}

	@Transactional
	public int toggleBookmarkByTripAndUserId(long userId, long tripId) {
		return bookmarkRepository.toggleTripBookmarkByTripIdAndUserId(userId, tripId);
	}

	@Transactional
	public void createTripBookmark(UserEntity user, Trip trip) {
		BookmarkId bookmarkId = BookmarkId.builder().tripId(trip.getId()).userId(user.getId()).build();

		Bookmark bookmark = Bookmark.builder().id(bookmarkId).trip(trip).user(user).toggle(true).build();

		bookmarkRepository.save(bookmark);
	}

	public boolean getIsToggleByUserIdAndScrapId(long userId, long tripId) {
		return bookmarkRepository.getIsToggleByUserIdAndTripId(userId, tripId).orElseThrow(NullPointerException::new);
	}

	@Transactional
	public TripInfoDto modifyTripByDto(Trip trip, TripModifyDto requestBody) {
		trip.setName(requestBody.getName());
		trip.setThumbnail(requestBody.getThumbnail());
		trip.setStartDate(requestBody.getStartDate());
		trip.setEndDate(requestBody.getEndDate());
		trip.setLabelColor(requestBody.getLabelColor());

		Trip modifiedTrip = tripRepository.save(trip);

		return new TripInfoDto(modifiedTrip);
	}

	public Trip findTripByTripId(long tripId) {
		return tripRepository.findTripDetailById(tripId).orElseThrow(NullPointerException::new);
	}

	public boolean isLeaderOfTrip(long userId, long tripId) {
		return tripMemberRepository.isLeaderOfTrip(userId, tripId);
	}

	public long findTripIdByInvitationCode(String invitationCode) {
		return tripRepository.findTrip_idByInvitationCode(invitationCode).orElseThrow(() -> new NoSuchElementException("Trip not found with invitation code: " + invitationCode));
	}

	public void deleteTripMember(long tripId, long userId) {
		tripMemberRepository.deleteById(
			TripMemberId.builder()
				.tripId(tripId)
				.userId(userId)
				.build()
		);
	}

	public int countTripMember(long tripId) {
		return tripMemberRepository.countTripMemberByTrip_Id(tripId);
	}

	public List<VisitedCountryDto> findVisitedCountry(long userId) {
		return tripRepository.findVisitedCountry(userId, LocalDate.now());
	}

	public void deleteTripMemberByUuid(long tripId, String uuid) {
		tripMemberRepository.deleteByTripIdAndUuid(tripId, uuid);
	}
}
