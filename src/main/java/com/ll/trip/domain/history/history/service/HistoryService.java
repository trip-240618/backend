package com.ll.trip.domain.history.history.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.history.history.dto.HistoryCreateRequestDto;
import com.ll.trip.domain.history.history.dto.HistoryDetailDto;
import com.ll.trip.domain.history.history.dto.HistoryListDto;
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
import com.ll.trip.domain.user.user.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {
	private final HistoryRepository historyRepository;
	private final HistoryTagRepository historyTagRepository;
	private final HistoryReplyRepository historyReplyRepository;
	private final HistoryLikeRepository historyLikeRepository;

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
	public boolean toggleHistoryLike(History history, UserEntity user) {
		Optional<HistoryLike> optLike = historyLikeRepository.findByHistoryIdAndUserId(history.getId(), user.getId());

		if (optLike.isEmpty()) {
			historyLikeRepository.save(
				HistoryLike.builder()
					.history(history)
					.user(user)
					.toggle(true)
					.build());
			return true;
		}

		HistoryLike like = optLike.get();
		boolean toggle = like.isToggle();

		historyLikeRepository.updateToggleById(like.getId(), !toggle);
		historyRepository.updateLikeCntById(history.getId(), toggle ? -1 : 1);

		return !toggle;
	}

	@Transactional
	public void deleteHistoryTag(long tagId) {
		historyTagRepository.deleteById(tagId);
	}

	@Transactional
	public void modifyHistoryReply(HistoryReply replyRef, String content) {
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
		if(tagColor != null) return historyRepository.findHistoryByTripIdAndTagNameAndColor(tripId, tagName, tagColor, pageable);
		else return historyRepository.findHistoryByTripIdAndTagName(tripId, tagName, pageable);
	}
}
