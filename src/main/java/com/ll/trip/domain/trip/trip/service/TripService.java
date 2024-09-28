package com.ll.trip.domain.trip.trip.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.history.history.entity.History;
import com.ll.trip.domain.trip.trip.dto.TripCreateDto;
import com.ll.trip.domain.trip.trip.dto.TripInfoDto;
import com.ll.trip.domain.trip.trip.dto.TripMemberDto;
import com.ll.trip.domain.trip.trip.entity.Bookmark;
import com.ll.trip.domain.trip.trip.entity.BookmarkId;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.domain.trip.trip.entity.TripMemberId;
import com.ll.trip.domain.trip.trip.repository.BookmarkRepository;
import com.ll.trip.domain.trip.trip.repository.TripMemberRepository;
import com.ll.trip.domain.trip.trip.repository.TripRepository;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.global.security.userDetail.SecurityUser;

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

		TripMemberId tripMemberId = TripMemberId.builder()
			.tripId(trip.getId())
			.userId(user.getId())
			.build();

		if (tripMemberRepository.existsById(tripMemberId)) {
			return false;
		}

		TripMember tripMember = TripMember.builder()
			.id(tripMemberId)
			.user(user)
			.trip(trip)
			.isLeader(isLeader)
			.build();

		tripMemberRepository.save(tripMember);
		return true;
	}

	public boolean existTripMemberByTripIdAndUserId(long tripId, long userId) {
		return tripMemberRepository.existsTripMemberByTripIdAndUserId(tripId, userId);
	}

	public List<TripInfoDto> findAllIncomingByUserId(Long userId, LocalDate date) {
		return tripRepository.findTripIncommingByUserIdAndDate(userId, date).stream().map(TripInfoDto::new).toList();
	}

	public List<TripInfoDto> findAllLastByUserIdAndDate(Long userId, LocalDate date) {
		return tripRepository.findTripLastByUserIdAndDate(userId, date).stream().map(TripInfoDto::new).toList();
	}

	public List<TripInfoDto> findBookmarkByUserId(Long userId) {
		return tripRepository.findAllBookmarkTrip(userId).stream().map(TripInfoDto::new).toList();
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
		BookmarkId bookmarkId = BookmarkId.builder().
			tripId(trip.getId())
			.userId(user.getId())
			.build();

		Bookmark bookmark = Bookmark.builder()
			.id(bookmarkId)
			.trip(trip)
			.user(user)
			.toggle(true)
			.build();

		bookmarkRepository.save(bookmark);
	}

	public boolean getIsToggleByUserIdAndScrapId(long userId, long tripId) {
		return bookmarkRepository.getIsToggleByUserIdAndTripId(userId, tripId)
			.orElseThrow(NullPointerException::new);
	}

	@Transactional
	public TripInfoDto modifyTripByDto(Trip trip, TripInfoDto requestBody) {
		trip.setName(requestBody.getName());
		trip.setThumbnail(requestBody.getThumbnail());
		trip.setStartDate(requestBody.getStartDate());
		trip.setEndDate(requestBody.getEndDate());

		Trip modifiedTrip = tripRepository.save(trip);

		return new TripInfoDto(modifiedTrip);
	}

	public Trip findTripDetailByTripId(long tripId) {
		return tripRepository.findTripDetailById(tripId).orElseThrow(NullPointerException::new);
	}

	public boolean isLeaderOfTrip(long userId, long tripId) {
		return tripMemberRepository.isLeaderOfTrip(userId, tripId);
	}

	public List<String> findImageByTripId(long tripId) {
		List<Trip> trips = tripRepository.findTripAndHistoryByTripId(tripId);

		List<String> urls = new ArrayList<>();

		for (Trip trip : trips) {
			urls.add(trip.getThumbnail());
			for (History history : trip.getHistories()) {
				urls.add(history.getImageUrl());
				urls.add(history.getThumbnail());
			}
		}

		return urls;
	}

	public long findTripIdByInvitationCode(String invitationCode) {
		return tripRepository.findTrip_idByInvitationCode(invitationCode);
	}

	public TripMemberDto makeTripMemberDto(SecurityUser securityUser) {
		return new TripMemberDto(securityUser.getUuid(), securityUser.getNickname(), securityUser.getThumbnail(),
			false);
	}
}
