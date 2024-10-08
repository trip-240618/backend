package com.ll.trip.domain.history.history.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.history.history.dto.HistoryCreateRequestDto;
import com.ll.trip.domain.history.history.dto.HistoryDetailDto;
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

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {
	private final TripRepository tripRepository;
	private final HistoryRepository historyRepository;
	private final HistoryTagRepository historyTagRepository;
	private final HistoryReplyRepository historyReplyRepository;
	private final HistoryLikeRepository historyLikeRepository;
	private final EntityManager entityManager;

	public List<HistoryListDto> findAllByTripId(long tripId) {
		Pageable pageable = PageRequest.of(0, 50);
		return historyRepository.findAllByTripId(tripId, pageable);
	}

	@Transactional
	public History createHistory(HistoryCreateRequestDto requestDto, UserEntity user, Trip trip) {
		History history = historyRepository.save(buildHistory(requestDto, user, trip));
		createHistoryTags(requestDto.getTags(), trip, history);
		return history;
	}

	@Transactional
	public List<HistoryTag> createHistoryTags(List<HistoryTagDto> tagDtos, Trip trip, History history) {
		List<HistoryTag> tags = tagDtos.stream().map(
			dto -> HistoryTag.builder()
				.tagName(dto.getTagName())
				.tagColor(dto.getTagColor())
				.trip(trip)
				.history(history)
				.build()
		).toList();

		return historyTagRepository.saveAll(tags);
	}

	public HistoryTag createHistoryTag(HistoryTagDto dto, Trip trip, History history) {
		HistoryTag tag = HistoryTag.builder()
			.tagName(dto.getTagName())
			.tagColor(dto.getTagColor())
			.trip(trip)
			.history(history)
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
	public List<HistoryListDto> createManyHistories(List<HistoryCreateRequestDto> dtos, UserEntity user, Trip trip) {
		for (HistoryCreateRequestDto dto : dtos) {
			createHistory(dto, user, trip);
		}
		return findAllByTripId(trip.getId());
	}

	@Transactional
	public void deleteHistory(long historyId) {
		historyRepository.deleteById(historyId);
	}

	public HistoryDetailDto showHistoryDetail(long historyId, long userId) {
		List<HistoryServiceDto> dtos = historyRepository.findHistoryById(historyId, userId);
		return convertToHistoryDetailDto(dtos);
	}

	public HistoryDetailDto convertToHistoryDetailDto(List<HistoryServiceDto> dtos) {
		HistoryDetailDto historyDetailDto = new HistoryDetailDto(dtos.remove(0));
		for (HistoryServiceDto dto : dtos) {
			historyDetailDto.getTags().add(dto.getTag());
		}
		return historyDetailDto;
	}

	@Transactional
	public void createHistoryReply(History history, UserEntity user, HistoryReplyCreateRequestDto requestDto) {
		HistoryReply reply = HistoryReply.builder()
			.user(user)
			.history(history)
			.content(requestDto.getContent())
			.build();

		historyReplyRepository.save(reply);
		historyRepository.updateReplyCntById(history.getId(), 1);
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

	public boolean isWriterOfHistory(long historyId, long userId) {
		return historyRepository.existsByHistoryIdAndUserId(historyId, userId);
	}

	@Transactional
	public boolean createHistoryLike(long historyId, long userId) {
		HistoryLike historyLike = historyLikeRepository.save(
			HistoryLike.builder()
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

	public List<HistoryListDto> searchHistoryByUuid(long tripId, String uuid) {
		Pageable pageable = PageRequest.of(0, 50);
		return historyRepository.findHistoryByTripIdAndUuid(tripId, uuid, pageable);
	}

	public List<HistoryListDto> searchHistoryByTagNameAndColor(long tripId, String tagName, String tagColor) {
		Pageable pageable = PageRequest.of(0, 50);
		if (tagColor != null)
			return historyRepository.findHistoryByTripIdAndTagNameAndColor(tripId, tagName, tagColor, pageable);
		else
			return historyRepository.findHistoryByTripIdAndTagName(tripId, tagName, pageable);
	}

	@Transactional
	public void modifyHistory(long tripId, long historyId, HistoryModifyDto requestDto) {
		Trip tripRef = tripRepository.getReferenceById(tripId);
		History historyRef = historyRepository.getReferenceById(historyId);
		List<HistoryTag> tags = createHistoryTags(requestDto.getTags(), tripRef, historyRef);
		historyRef.setHistoryTags(tags);
		historyRef.setMemo(requestDto.getMemo());
		historyRepository.save(historyRef);
	}

	public HistoryLike findHistoryLikeByHistoryIdAndUserId(long historyId, long userId) {
		return historyLikeRepository.findByHistoryIdAndUserId(historyId, userId).orElse(null);
	}
}
