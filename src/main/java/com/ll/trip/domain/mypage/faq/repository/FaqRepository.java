package com.ll.trip.domain.mypage.faq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.mypage.faq.dto.FaqListDto;
import com.ll.trip.domain.mypage.faq.entity.Faq;

public interface FaqRepository extends JpaRepository<Faq, Long> {

	@Query("""
		SELECT new com.ll.trip.domain.mypage.faq.dto.FaqListDto(f.id, f.title, f.type) FROM Faq f
		WHERE f.type = :type
		order by f.createDate desc
		""")
	List<FaqListDto> findByType(String type);

	@Query("""
		SELECT new com.ll.trip.domain.mypage.faq.dto.FaqListDto(f.id, f.title, f.type) FROM Faq f
		WHERE f.title LIKE %:text%
		OR f.content LIKE %:text%
		order by f.createDate desc
		""")
	List<FaqListDto> findByTextLike(String text);

	@Query("""
		SELECT new com.ll.trip.domain.mypage.faq.dto.FaqListDto(f.id, f.title, f.type) FROM Faq f
		order by f.createDate desc
		""")
	List<FaqListDto> findAllDto();

	@Modifying
	@Query("""
		update Faq f
		set f.content = :content,
		f.type = :type,
		f.title = :title
		where f.id = :faqId
		""")
	void modifyFaq(long faqId, String content, String type, String title);
}
