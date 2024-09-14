package com.ll.trip.domain.trip.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.history.entity.History;
import com.ll.trip.domain.trip.trip.entity.Trip;

public interface HistoryRepository extends JpaRepository<History, Long> {

	@Query("""
		    SELECT DISTINCT h
		    FROM History h
		    JOIN FETCH h.user u
		    LEFT JOIN FETCH h.historyTags ht
		    WHERE h.trip.id = :tripId
		    ORDER BY ht.id ASC
		""")
	public List<History> findAllByTripId(Long tripId);

	long countByTrip(Trip trip);
}
