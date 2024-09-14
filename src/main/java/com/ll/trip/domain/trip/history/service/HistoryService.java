package com.ll.trip.domain.trip.history.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.history.dto.HistoryCreateRequestDto;
import com.ll.trip.domain.trip.history.dto.HistoryListDto;
import com.ll.trip.domain.trip.history.entity.History;
import com.ll.trip.domain.trip.history.entity.HistoryTag;
import com.ll.trip.domain.trip.history.repository.HistoryRepository;
import com.ll.trip.domain.trip.history.repository.HistoryTagRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.user.user.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {
	private final HistoryRepository historyRepository;
	private final HistoryTagRepository historyTagRepository;

	public List<HistoryListDto> findAllByTripId(long tripId) {
		return historyRepository.findAllByTripId(tripId).stream()
			.map(HistoryListDto::new).toList();
	}

	@Transactional
	public void createHistory(HistoryCreateRequestDto requestDto, UserEntity user, Trip trip) {

		History history = historyRepository.save(History.builder()
			.imageUrl(requestDto.getImageUrl())
			.thumbnail(requestDto.getThumbnail())
			.latitude(requestDto.getLatitude())
			.longitude(requestDto.getLongitude())
			.memo(requestDto.getMemo())
			.photoDate(requestDto.getPhotoDate())
			.user(user)
			.trip(trip)
			.build()
		);

		List<HistoryTag> tags = requestDto.getTags().stream().map(tag -> {
			return HistoryTag.builder()
				.tag_name(tag)
				.trip(trip)
				.history(history)
				.build();
		}).toList();

		historyTagRepository.saveAll(tags);
	}
}
