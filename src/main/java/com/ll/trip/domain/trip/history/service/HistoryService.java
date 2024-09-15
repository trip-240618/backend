package com.ll.trip.domain.trip.history.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.history.dto.HistoryCreateRequestDto;
import com.ll.trip.domain.trip.history.dto.HistoryDetailDto;
import com.ll.trip.domain.trip.history.dto.HistoryListDto;
import com.ll.trip.domain.trip.history.dto.HistoryReplyCreateRequestDto;
import com.ll.trip.domain.trip.history.dto.HistoryReplyDto;
import com.ll.trip.domain.trip.history.entity.History;
import com.ll.trip.domain.trip.history.entity.HistoryLike;
import com.ll.trip.domain.trip.history.entity.HistoryReply;
import com.ll.trip.domain.trip.history.entity.HistoryTag;
import com.ll.trip.domain.trip.history.repository.HistoryLikeRepository;
import com.ll.trip.domain.trip.history.repository.HistoryReplyRepository;
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
	private final HistoryReplyRepository historyReplyRepository;
	private final HistoryLikeRepository historyLikeRepository;

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

	@Transactional
	public void deleteHistory(long historyId) {
		historyRepository.deleteById(historyId);
	}

	public HistoryDetailDto showHistoryDetail(long historyId) {
		History history = historyRepository.findHistoryById(historyId);
		return new HistoryDetailDto(history);
	}

	@Transactional
	public void createHistoryReply(History history, UserEntity user, HistoryReplyCreateRequestDto requestDto) {
		HistoryReply reply = HistoryReply.builder()
			.user(user)
			.history(history)
			.writerUuid(user.getUuid())
			.content(requestDto.getContent())
			.build();

		historyReplyRepository.save(reply);
	}

	public List<HistoryReplyDto> showHistoryReplyList(long historyId) {
		return historyReplyRepository.findByHistoryId(historyId);
	}

	public History findById(long historyId) {
		return historyRepository.findById(historyId).orElseThrow(NullPointerException::new);
	}

	public boolean isWriterOfReply(long historyId, long userId) {
		return historyReplyRepository.existsByHistoryIdAndUserId(historyId, userId);
	}

	@Transactional
	public void deleteHistoryReply(long replyId) {
		historyReplyRepository.deleteById(replyId);
	}

	public boolean isWriterOfHistory(long historyId, long userId) {
		return historyRepository.existsByHistoryIdAndUserId(historyId, userId);
	}

	@Transactional
	public boolean toggleHistoryLike(long historyId, long userId) {
		Optional<HistoryLike> optLike = historyLikeRepository.findByHistoryIdAndUserId(historyId, userId);
		HistoryLike like;

		if (optLike.isEmpty()) {
			like = HistoryLike.builder()
				.historyId(historyId)
				.userId(userId)
				.toggle(true)
				.build();

			historyLikeRepository.save(like);
			return true;
		}

		like = optLike.get();
		boolean toggle = like.isToggle();

		int likeUpdated = historyLikeRepository.updateToggleById(like.getId(), !toggle);
		int historyUpdated = historyRepository.updateLikeCntById(historyId, toggle? 1 : -1);

		return !toggle;
	}
}
