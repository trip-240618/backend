package com.ll.trip.domain.notification.notice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.notification.notice.dto.NoticeListDto;
import com.ll.trip.domain.notification.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
	@Query("""
		select new com.ll.trip.domain.notification.notice.dto.NoticeListDto(
		n.type, n.title, n.createDate
		)
		from Notice n
		where (:type IS NULL OR n.type = :type)
		""")
	List<NoticeListDto> findNoticeList(String type);
}
