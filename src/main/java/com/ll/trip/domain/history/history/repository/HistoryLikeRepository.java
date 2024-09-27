package com.ll.trip.domain.history.history.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.history.history.entity.HistoryLike;

public interface HistoryLikeRepository extends JpaRepository<HistoryLike, Long> {

	Optional<HistoryLike> findByHistoryIdAndUserId(long historyId, long userId);

	@Query("""
		update HistoryLike l
		set l.toggle = :toggle
		where l.id = :id
		""")
	int updateToggleById(long id, boolean toggle);
}
