package com.ll.trip.domain.history.history.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.history.history.dto.HistoryCreateRequestDto;
import com.ll.trip.domain.history.history.dto.HistoryDto;
import com.ll.trip.domain.history.history.dto.HistoryListDto;
import com.ll.trip.domain.history.history.dto.HistoryModifyDto;
import com.ll.trip.domain.history.history.dto.HistoryReplyCreateRequestDto;
import com.ll.trip.domain.history.history.dto.HistoryReplyDto;
import com.ll.trip.domain.history.history.dto.HistoryServiceDto;
import com.ll.trip.domain.history.history.dto.HistoryTagDto;
import com.ll.trip.domain.history.history.entity.History;
import com.ll.trip.domain.history.history.entity.HistoryLike;
import com.ll.trip.domain.history.history.entity.HistoryReply;
import com.ll.trip.domain.history.history.entity.HistoryTag;
import com.ll.trip.domain.history.history.repository.HistoryLikeRepository;
import com.ll.trip.domain.history.history.repository.HistoryReplyRepository;
import com.ll.trip.domain.history.history.repository.HistoryRepository;
import com.ll.trip.domain.history.history.repository.HistoryTagRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.repository.TripRepository;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.global.handler.exception.PermissionDeniedException;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HistoryService {
	private final TripRepository tripRepository;
	private final HistoryRepository historyRepository;
	private final HistoryTagRepository historyTagRepository;
	private final HistoryReplyRepository historyReplyRepository;
	private final HistoryLikeRepository historyLikeRepository;
	private final EntityManager entityManager;

	public List<HistoryListDto> findAllByTripId(long tripId, long userId) {
		Pageable pageable = PageRequest.of(0, 50);
		List<HistoryServiceDto> serviceDtos = historyRepository.findAllByTripId(tripId, userId, pageable);
		return parseToResponse(serviceDtos);
	}

	private List<HistoryListDto> parseToResponse(List<HistoryServiceDto> serviceDtos) {
		List<HistoryListDto> response = new ArrayList<>();
		Map<Long, HistoryDto> idMap = new HashMap<>();
		Map<LocalDate, List<HistoryDto>> dateMap = new HashMap<>();

		for (HistoryServiceDto dto : serviceDtos) {
			HistoryDto historyDto = idMap.computeIfAbsent(dto.getId(), id -> {
				HistoryDto newHistoryDto = new HistoryDto(dto);

				dateMap.computeIfAbsent(dto.getPhotoDate(), date -> {
					HistoryListDto ListDto = new HistoryListDto(date);
					response.add(ListDto);
					return ListDto.getHistoryList();
				}).add(newHistoryDto);

				return newHistoryDto;
			});
			historyDto.getTags().add(dto.getTag());
		}
		return response;
	}

	@Transactional
	public History createHistory(HistoryCreateRequestDto requestDto, Trip trip, UserEntity user) {
		History history = historyRepository.save(buildHistory(requestDto, user, trip));
		createHistoryTags(requestDto.getTags(), trip, history);
		return history;
	}

	@Transactional
	public List<HistoryTag> createHistoryTags(List<HistoryTagDto> tagDtos, Trip trip, History history) {
		List<HistoryTag> tags = tagDtos.stream()
			.map(dto -> HistoryTag.builder()
				.tagName(dto.getTagName())
				.tagColor(dto.getTagColor())
				.trip(trip)
				.history(history)
				.build())
			.toList();

		return historyTagRepository.saveAll(tags);
	}

	@Transactional
	public HistoryTag createHistoryTag(HistoryTagDto dto, long tripId, long historyId) {
		HistoryTag tag = HistoryTag.builder()
			.tagName(dto.getTagName())
			.tagColor(dto.getTagColor())
			.trip(entityManager.getReference(Trip.class, tripId))
			.history(entityManager.getReference(History.class, historyId))
			.build();

		return historyTagRepository.save(tag);
	}

	@Transactional
	public History buildHistory(HistoryCreateRequestDto requestDto, UserEntity user, Trip trip) {
		return History.builder()
			.imageUrl(requestDto.getImageUrl())
			.thumbnail(requestDto.getThumbnail())
			.latitude(requestDto.getLatitude())
			.longitude(requestDto.getLongitude())
			.memo(requestDto.getMemo())
			.photoDate(requestDto.getPhotoDate())
			.likeCnt(0)
			.replyCnt(0)
			.user(user)
			.trip(trip)
			.build();
	}

	@Transactional
	public List<HistoryListDto> createManyHistories(List<HistoryCreateRequestDto> dtos, long tripId,
		long userId) {
		UserEntity user = entityManager.getReference(UserEntity.class, userId);
		Trip trip = entityManager.getReference(Trip.class, tripId);
		for (HistoryCreateRequestDto dto : dtos) {
			createHistory(dto, trip, user);
		}
		return findAllByTripId(trip.getId(), userId);
	}

	@Transactional
	public void deleteHistory(long historyId) {
		historyRepository.deleteById(historyId);
	}

	@Transactional
	public void createHistoryReply(long historyId, long userId, HistoryReplyCreateRequestDto requestDto) {
		HistoryReply reply = HistoryReply.builder()
			.user(entityManager.getReference(UserEntity.class, userId))
			.history(entityManager.getReference(History.class, historyId))
			.content(requestDto.getContent())
			.build();

		historyReplyRepository.save(reply);
		historyRepository.updateReplyCntById(historyId, 1);
	}

	public List<HistoryReplyDto> showHistoryReplyList(long historyId) {
		return historyReplyRepository.findByHistoryId(historyId);
	}

	public boolean isWriterOfReply(long replyId, long userId) {
		return historyReplyRepository.existsByReplyIdAndUserId(replyId, userId);
	}

	@Transactional
	public void deleteHistoryReply(long historyId, long replyId) {
		historyReplyRepository.deleteById(replyId);
		historyRepository.updateReplyCntById(historyId, -1);
	}

	public void checkIsWriterOfHistory(long historyId, long userId) {
		if (!historyRepository.existsByHistoryIdAndUserId(historyId, userId)) {
			log.info("user: " + userId + " isn't writer of history: " + historyId);
			throw new PermissionDeniedException("user isn't writer of history");
		}

	}

	@Transactional
	public boolean createHistoryLike(long historyId, long userId) {
		HistoryLike historyLike = historyLikeRepository.save(HistoryLike.builder()
			.history(entityManager.getReference(History.class, historyId))
			.user(entityManager.getReference(UserEntity.class, userId))
			.toggle(true)
			.build());
		historyRepository.updateLikeCntById(historyId, 1);
		return historyLike.isToggle();
	}

	@Transactional
	public boolean toggleHistoryLike(long historyId, long userId, HistoryLike like) {
		boolean toggle = like.isToggle();

		historyLikeRepository.updateToggleById(like.getId(), !toggle);
		historyRepository.updateLikeCntById(historyId, toggle ? -1 : 1);

		return !toggle;
	}

	@Transactional
	public void deleteHistoryTag(long tagId) {
		historyTagRepository.deleteById(tagId);
	}

	@Transactional
	public void modifyHistoryReply(long replyId, String content) {
		HistoryReply replyRef = entityManager.getReference(HistoryReply.class, replyId);
		replyRef.setContent(content);
		historyReplyRepository.save(replyRef);
	}

	public List<HistoryTagDto> showAllTagsByTripId(long tripId) {
		return historyTagRepository.findAllTagsByTripId(tripId);
	}

	public List<HistoryDto> searchHistoryByUuid(long tripId, long userId, String uuid) {
		Pageable pageable = PageRequest.of(0, 50);
		List<HistoryServiceDto> serviceDtos = historyRepository.findHistoryByTripIdAndUuid(tripId, userId, uuid,
			pageable);
		return serviceDtos.stream().map(HistoryDto::new).toList();
	}

	public List<HistoryDto> searchHistoryByTagNameAndColor(long tripId, long userId, String tagName,
		String tagColor) {
		Pageable pageable = PageRequest.of(0, 50);
		List<HistoryServiceDto> serviceDtos;
		if (tagColor != null)
			serviceDtos = historyRepository.findHistoryByTripIdAndTagNameAndColor(tripId, userId, tagName, tagColor,
				pageable);
		else
			serviceDtos = historyRepository.findHistoryByTripIdAndTagName(tripId, userId, tagName, pageable);
		return serviceDtos.stream().map(HistoryDto::new).toList();
	}

	@Transactional
	public void modifyHistory(long tripId, History history, HistoryModifyDto requestDto) {
		Trip tripRef = tripRepository.getReferenceById(tripId);
		List<HistoryTag> tags = createHistoryTags(requestDto.getTags(), tripRef, history);
		history.setHistoryTags(tags);
		history.setMemo(requestDto.getMemo());
		historyRepository.save(history);
	}

	public HistoryLike findHistoryLikeByHistoryIdAndUserId(long historyId, long userId) {
		return historyLikeRepository.findByHistoryIdAndUserId(historyId, userId).orElse(null);
	}

	public History findById(long historyId) {
		return historyRepository.findById(historyId).orElseThrow(NoSuchElementException::new);
	}

	public List<HistoryListDto> showHistoryDetail(long historyId, long userId) {
		List<HistoryServiceDto> dtos = historyRepository.findHistoryById(historyId, userId);
		return parseToResponse(dtos);
	}
}
