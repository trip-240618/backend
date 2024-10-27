package com.ll.trip.domain.notification.notice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.notification.notice.repository.NoticeRepository;
import com.ll.trip.domain.notification.notice.dto.NoticeCreateDto;
import com.ll.trip.domain.notification.notice.dto.NoticeDetailDto;
import com.ll.trip.domain.notification.notice.dto.NoticeListDto;
import com.ll.trip.domain.notification.notice.entity.Notice;
import com.ll.trip.global.handler.exception.NoSuchDataException;

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
		if (type == null)
			return noticeRepository.findAllDto();
		return noticeRepository.findNoticeList(type);
	}

	@Transactional
	public NoticeDetailDto modifyNotice(long noticeId, NoticeCreateDto dto) {
		noticeRepository.modifyNotice(noticeId, dto.getContent(), dto.getType(), dto.getDuration(), dto.getTitle(),
			dto.getReason());
		return showNoticeDetail(noticeId);
	}

	@Transactional
	public void deleteNotice(long noticeId) {
		noticeRepository.deleteById(noticeId);
	}

	public NoticeDetailDto showNoticeDetail(long noticeId) {
		Notice notice = (noticeRepository.findById(noticeId).orElseThrow(
			() -> new NoSuchDataException("can't find such data, noticeId: " + noticeId)
		));
		return new NoticeDetailDto(notice);
	}
}
