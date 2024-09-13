package com.ll.trip.domain.trip.trip.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.trip.dto.TripCreateDto;
import com.ll.trip.domain.trip.trip.dto.TripInfoDto;
import com.ll.trip.domain.trip.trip.dto.TripMemberDto;
import com.ll.trip.domain.trip.trip.dto.TripMemberServiceDto;
import com.ll.trip.domain.trip.trip.entity.Bookmark;
import com.ll.trip.domain.trip.trip.entity.BookmarkId;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.domain.trip.trip.entity.TripMemberId;
import com.ll.trip.domain.trip.trip.repository.BookmarkRepository;
import com.ll.trip.domain.trip.trip.repository.TripMemberRepository;
import com.ll.trip.domain.trip.trip.repository.TripRepository;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripService {

	private final TripRepository tripRepository;
	private final TripMemberRepository tripMemberRepository;
	private final InvitationCodeGenerator invitationCodeGenerator;

	//bookmark
	private final BookmarkRepository bookmarkRepository;
	private final UserRepository userRepository;

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

		return trip = tripRepository.save(trip);
	}

	public String generateInvitationCode() {
		return invitationCodeGenerator.generateUniqueCode();
	}

	public void joinTripById(Trip trip, UserEntity user, boolean isLeader) {
		TripMemberId tripMemberId = TripMemberId.builder()
			.tripId(trip.getId())
			.userId(user.getId())
			.build();

		if (tripMemberRepository.existsById(tripMemberId)) {
			return;
		}

		TripMember tripMember = TripMember.builder()
			.id(tripMemberId)
			.user(user)
			.trip(trip)
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

	public List<TripInfoDto> findAllByUserId(Long userId, LocalDate date, String type, String sortDirection,
		String sortField) {

		return tripRepository.findTripInfosWithDynamicSort(userId, LocalDate.now(), sortField, sortDirection, type);
	}

	public void fillTripMemberToTripInfo(List<TripInfoDto> tripInfoDtoList) {
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
				tripMemberServiceDto.getUuid(),
				tripMemberServiceDto.getNickname(),
				tripMemberServiceDto.getProfileImg(),
				tripMemberServiceDto.isLeader()
			);

			tripMap.get(id).getTripMemberDtoList().add(tripMemberDto);
		}
	}

	public List<TripInfoDto> findBookmarkByUserId(Long userId, String sortDirection, String sortField) {
		return tripRepository.findBookmarkTripInfosWithDynamicSort(userId, sortField, sortDirection);
	}

	@Transactional
	public void deleteTripById(Long id) {
		tripRepository.deleteById(id);
	}

	@Transactional
	public boolean toggleBookmarkByTripAndUserId(Trip trip, Long userId) {
		BookmarkId bookmarkId = BookmarkId.builder().
			tripId(trip.getId())
			.userId(userId)
			.build();

		Optional<Bookmark> optBookmark = bookmarkRepository.findById(bookmarkId);

		if (optBookmark.isEmpty()) {
			UserEntity user = userRepository.findById(userId).orElseThrow(NullPointerException::new);

			Bookmark bookmark = Bookmark.builder()
				.id(bookmarkId)
				.trip(trip)
				.user(user)
				.toggle(true)
				.build();

			return true;
		}

		Bookmark bookmark = optBookmark.get();
		boolean toggle = bookmark.isToggle();

		if (bookmarkRepository.updateToggleById(bookmarkId, !toggle) > 0)
			return !toggle; //수정됨
		else
			return toggle;
	}

	public TripInfoDto modifyTripByDto(Trip trip, TripInfoDto requestBody) {
		trip.setName(requestBody.getName());
		trip.setThumbnail(requestBody.getThumbnail());
		trip.setStartDate(requestBody.getStartDate());
		trip.setEndDate(requestBody.getEndDate());

		Trip modifiedTrip = tripRepository.save(trip);

		return new TripInfoDto(modifiedTrip);
	}
}
