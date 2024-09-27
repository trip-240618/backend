package com.ll.trip.domain.history.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.history.history.entity.History;
import com.ll.trip.domain.trip.trip.entity.Trip;

public interface HistoryRepository extends JpaRepository<History, Long> {

	@Query("""
		    SELECT DISTINCT h
		    FROM History h
		    JOIN fetch h.user u
		    LEFT JOIN fetch h.historyTags ht
		    WHERE h.trip.id = :tripId
		    ORDER BY ht.id ASC
		""")
	public List<History> findAllByTripId(Long tripId);

	long countByTrip(Trip trip);

	@Query("""
		    SELECT DISTINCT h
		    FROM History h
		    JOIN fetch h.user u
		    LEFT JOIN fetch h.historyTags ht
		    LEFT JOIN fetch h.historyReplies hr
		    WHERE h.id = :historyId
		    ORDER BY ht.id ASC, hr.createDate asc
		""")
	History findHistoryById(long historyId);

	@Query("""
			SELECT CASE WHEN COUNT(h) > 0 THEN TRUE ELSE FALSE END
		     FROM History h
		     WHERE h.id = :historyId AND h.user.id = :userId
		""")
	boolean existsByHistoryIdAndUserId(long historyId, long userId);

	@Query("""
			update History h
		 	set h.likeCnt = h.likeCnt + :i
		 	where h.id = :historyId
	""")
	int updateLikeCntById(long historyId, int i);
}
