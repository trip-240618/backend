package com.ll.trip.domain.notification.notice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.notification.notice.repository.NoticeRepository;
import com.ll.trip.domain.notification.notice.dto.NoticeCreateDto;
import com.ll.trip.domain.notification.notice.dto.NoticeDetailDto;
import com.ll.trip.domain.notification.notice.dto.NoticeListDto;
import com.ll.trip.domain.notification.notice.entity.Notice;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {
	private final NoticeRepository noticeRepository;

	@Transactional
	public NoticeDetailDto createNotice(NoticeCreateDto noticeCreateDto) {
		Notice notice = noticeRepository.save(
			Notice.builder()
				.type(noticeCreateDto.getType())
				.title(noticeCreateDto.getTitle())
				.content(noticeCreateDto.getContent())
				.duration(noticeCreateDto.getDuration())
				.reason(noticeCreateDto.getReason())
				.build()
		);

		return new NoticeDetailDto(notice);
	}

	public List<NoticeListDto> showNoticeList(String type) {
		if(type == null) return noticeRepository.findAllDto();
		return noticeRepository.findNoticeList(type);
	}

	@Transactional
	public NoticeDetailDto modifyNotice(long noticeId, NoticeCreateDto dto) {
		Notice noticeRef = noticeRepository.getReferenceById(noticeId);
		if (!dto.getContent().isBlank())
			noticeRef.setContent(dto.getContent());
		if (!dto.getType().isBlank())
			noticeRef.setType(dto.getType());
		if (!dto.getDuration().isBlank())
			noticeRef.setDuration(dto.getDuration());
		if (!dto.getTitle().isBlank())
			noticeRef.setTitle(dto.getTitle());
		if (!dto.getReason().isBlank())
			noticeRef.setReason(dto.getReason());
		return new NoticeDetailDto(noticeRepository.save(noticeRef));
	}

	@Transactional
	public void deleteNotice(long noticeId) {
		noticeRepository.deleteById(noticeId);
	}
}
