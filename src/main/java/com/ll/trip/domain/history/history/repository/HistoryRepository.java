package com.ll.trip.domain.history.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.history.history.dto.HistoryServiceDto;
import com.ll.trip.domain.history.history.entity.History;
import com.ll.trip.domain.trip.trip.entity.Trip;

public interface HistoryRepository extends JpaRepository<History, Long> {

	@Query("""
		    SELECT h
		    FROM History h
		    JOIN fetch h.user u
		    LEFT JOIN fetch h.historyTags ht
		    WHERE h.trip.id = :tripId
		    ORDER BY h.id, ht.id ASC
		""")
	List<History> findAllByTripId(Long tripId);

	long countByTrip(Trip trip);

	@Query("""
		    select new com.ll.trip.domain.history.history.dto.HistoryServiceDto(h.id, u.uuid, u.thumbnail, h.imageUrl, h.latitude,
		    h.longitude, h.memo, h.likeCnt, h.replyCnt, coalesce(l.toggle, false) , t.tagColor, t.tagName)
		    from History h
		    inner join UserEntity u on h.id = :historyId and h.user.id = u.id
		    left join h.historyTags t
		    left join HistoryLike l on l.history.id = h.id and l.user.id = :userId
		""")
	List<HistoryServiceDto> findHistoryById(long historyId, long userId);

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

	@Query("""
			update History h
		 	set h.replyCnt = h.replyCnt + :i
		 	where h.id = :historyId
	""")
	int updateReplyCntById(long historyId, int i);
}
