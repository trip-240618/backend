package com.ll.trip.domain.history.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.history.history.dto.HistoryReplyDto;
import com.ll.trip.domain.history.history.entity.HistoryReply;

public interface HistoryReplyRepository extends JpaRepository<HistoryReply, Long> {
	@Query("""
		select new com.ll.trip.domain.history.history.dto.HistoryReplyDto(r.id, u.uuid, u.profileImg, u.nickname, r.createDate, r.modifyDate, r.content)
		from HistoryReply r
		inner join r.user u on r.history.id = :historyId
		""")
	List<HistoryReplyDto> findByHistoryId(long historyId);

	@Query("""
			SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
		     FROM HistoryReply r
		     WHERE r.id = :replyId AND r.user.id = :userId
		""")
	boolean existsByReplyIdAndUserId(long replyId, long userId);

}
