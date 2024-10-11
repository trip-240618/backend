package com.ll.trip.domain.history.history.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.history.history.dto.HistoryListServiceDto;
import com.ll.trip.domain.history.history.dto.HistoryServiceDto;
import com.ll.trip.domain.history.history.entity.History;
import com.ll.trip.domain.trip.trip.entity.Trip;

public interface HistoryRepository extends JpaRepository<History, Long> {

	@Query("""
    SELECT new com.ll.trip.domain.history.history.dto.HistoryListServiceDto( h.id, u.uuid, u.thumbnail, h.imageUrl, h.thumbnail, h.latitude,
        h.longitude, h.photoDate,  h.memo, h.likeCnt, h.replyCnt, t.id, t.tagColor, t.tagName)
    FROM History h
    inner JOIN h.user u on h.trip.id = :tripId
    left join h.historyTags t
    ORDER BY h.photoDate ASC, h.id DESC
    """)
	List<HistoryListServiceDto> findAllByTripId(Long tripId, Pageable pageable);

	long countByTrip(Trip trip);

	@Query("""
		    select new com.ll.trip.domain.history.history.dto.HistoryServiceDto(
		    h.id, u.uuid, u.thumbnail, h.imageUrl, h.latitude,
		    h.longitude, h.memo, h.likeCnt, h.replyCnt, coalesce(l.toggle, false) , t.id, t.tagColor, t.tagName)
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

	@Modifying
	@Query("""
			update History h
		 	set h.likeCnt = h.likeCnt + :i
		 	where h.id = :historyId
	""")
	int updateLikeCntById(long historyId, int i);

	@Modifying
	@Query("""
			update History h
		 	set h.replyCnt = h.replyCnt + :i
		 	where h.id = :historyId
	""")
	int updateReplyCntById(long historyId, int i);

	@Query("""
    SELECT new com.ll.trip.domain.history.history.dto.HistoryListServiceDto(
    	h.id, u.uuid, u.thumbnail, h.imageUrl, h.thumbnail, h.latitude,
        h.longitude, h.photoDate, h.memo, h.likeCnt, h.replyCnt, t.id, t.tagColor, t.tagName)
    FROM History h
    inner JOIN h.user u on h.trip.id = :tripId and u.uuid = :uuid
    left join h.historyTags t
    ORDER BY h.photoDate ASC, h.id DESC
    """)
	List<HistoryListServiceDto> findHistoryByTripIdAndUuid(long tripId, String uuid, Pageable pageable);

	@Query("""
    SELECT distinct new com.ll.trip.domain.history.history.dto.HistoryListServiceDto(
    	h.id, u.uuid, u.thumbnail, h.imageUrl, h.thumbnail, h.latitude,
        h.longitude, h.photoDate, h.memo, h.likeCnt, h.replyCnt, t.id, t.tagColor, t.tagName)
    FROM History h
    inner join h.historyTags t on t.trip.id = :tripId and t.tagName = :tagName and t.tagColor = :tagColor
    left JOIN h.user u
    ORDER BY h.photoDate ASC, h.id DESC
    """)
	List<HistoryListServiceDto> findHistoryByTripIdAndTagNameAndColor(long tripId, String tagName, String tagColor, Pageable pageable);

	@Query("""
    SELECT distinct new com.ll.trip.domain.history.history.dto.HistoryListServiceDto(
    	h.id, u.uuid, u.thumbnail, h.imageUrl, h.thumbnail, h.latitude,
        h.longitude, h.photoDate, h.memo, h.likeCnt, h.replyCnt, t.id, t.tagColor, t.tagName)
    FROM History h
    inner join h.historyTags t on t.trip.id = :tripId and t.tagName = :tagName
    left JOIN h.user u
    ORDER BY h.photoDate ASC, h.id DESC
    """)
	List<HistoryListServiceDto> findHistoryByTripIdAndTagName(long tripId, String tagName, Pageable pageable);
}
