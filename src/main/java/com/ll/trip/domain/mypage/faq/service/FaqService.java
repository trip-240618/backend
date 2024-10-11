package com.ll.trip.domain.mypage.faq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.mypage.faq.dto.FaqCreateDto;
import com.ll.trip.domain.mypage.faq.dto.FaqListDto;
import com.ll.trip.domain.mypage.faq.entity.Faq;
import com.ll.trip.domain.mypage.faq.repository.FaqRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqService {
	private final FaqRepository faqRepository;

	@Transactional
	public Faq createFaq(FaqCreateDto faqCreateDto) {
		return faqRepository.save(
			Faq.builder()
				.type(faqCreateDto.getType())
				.title(faqCreateDto.getTitle())
				.content(faqCreateDto.getContent())
				.build()
		);
	}

	public List<FaqListDto> findFaqByType(String type) {
		if (type == null)
			return faqRepository.findAllDto();
		return faqRepository.findByType(type);
	}

	@Transactional
	public Faq modifyFaq(long faqId, FaqCreateDto dto) {
		Faq faqRef = faqRepository.getReferenceById(faqId);
		if (!dto.getContent().isBlank())
			faqRef.setContent(dto.getContent());
		if (!dto.getType().isBlank())
			faqRef.setType(dto.getType());
		if (!dto.getTitle().isBlank())
			faqRef.setTitle(dto.getTitle());
		return faqRepository.save(faqRef);
	}

	@Transactional
	public void deleteFaq(long faqId) {
		faqRepository.deleteById(faqId);
	}

	public List<FaqListDto> findFaqByText(String text) {
		return faqRepository.findByTextLike(text);
	}

	public Faq findFaqById(long faqId) {
		return faqRepository.findById(faqId).orElseThrow(NullPointerException::new);
	}
}
