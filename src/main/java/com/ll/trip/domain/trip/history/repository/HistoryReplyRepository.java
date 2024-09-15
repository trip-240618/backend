package com.ll.trip.domain.trip.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.history.dto.HistoryReplyDto;
import com.ll.trip.domain.trip.history.entity.HistoryReply;

public interface HistoryReplyRepository extends JpaRepository<HistoryReply, Long> {

	@Query("""
		select new com.ll.trip.domain.trip.history.dto.HistoryReplyDto(
			r.id,
			r.writerUuid,
			r.createDate,
			r.content
		) from HistoryReply r
		where r.history.id = :historyId
		""")
	List<HistoryReplyDto> findByHistoryId(long historyId);

	@Query("""
			SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
		     FROM HistoryReply r
		     WHERE r.history.id = :historyId AND r.user.id = :userId
		""")
	boolean existsByHistoryIdAndUserId(long historyId, long userId);
}
