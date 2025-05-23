package com.ll.trip.domain.history.history.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.history.history.entity.HistoryLike;

public interface HistoryLikeRepository extends JpaRepository<HistoryLike, Long> {

	@Query("""
				select l
				from HistoryLike l
				where l.user.id = :userId
				and l.history.id = :historyId
		""")
	Optional<HistoryLike> findByHistoryIdAndUserId(long historyId, long userId);

	@Modifying
	@Query("""
		update HistoryLike l
		set l.toggle = :toggle
		where l.id = :id
		""")
	int updateToggleById(long id, boolean toggle);
}
