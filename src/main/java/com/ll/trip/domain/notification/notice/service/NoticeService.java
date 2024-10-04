package com.ll.trip.domain.notification.notice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ll.trip.domain.notification.notice.NoticeRepository;
import com.ll.trip.domain.notification.notice.dto.NoticeCreateDto;
import com.ll.trip.domain.notification.notice.dto.NoticeDetailDto;
import com.ll.trip.domain.notification.notice.dto.NoticeListDto;
import com.ll.trip.domain.notification.notice.entity.Notice;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {
	private final NoticeRepository noticeRepository;

	public NoticeDetailDto createNotice(NoticeCreateDto noticeCreateDto) {
		Notice notice = noticeRepository.save(
			Notice.builder()
				.type(noticeCreateDto.getType())
				.title(noticeCreateDto.getTitle())
				.content(noticeCreateDto.getContent())
				.markdownDetails(noticeCreateDto.getMarkdownDetails())
				.build()
		);

		return new NoticeDetailDto(notice);
	}

	public List<NoticeListDto> showNoticeList(Boolean normal, Boolean update, Boolean system) {
	}
}
