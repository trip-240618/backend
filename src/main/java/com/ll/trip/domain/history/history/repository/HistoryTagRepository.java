package com.ll.trip.domain.history.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.history.history.dto.HistoryTagDto;
import com.ll.trip.domain.history.history.entity.HistoryTag;

public interface HistoryTagRepository extends JpaRepository<HistoryTag, Long> {
	@Query("""
		SELECT DISTINCT new com.ll.trip.domain.history.history.dto.HistoryTagDto(t.tagColor, t.tagName)
		FROM HistoryTag t
		where t.trip.id = :tripId
		""")
	List<HistoryTagDto> findAllTagsByTripId(long tripId);
}
