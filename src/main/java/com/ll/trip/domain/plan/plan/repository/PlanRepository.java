package com.ll.trip.domain.plan.plan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.plan.plan.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {
	// roomId로 검색하고 index로 정렬하는 커스텀 쿼리

	@Query("SELECT p FROM Plan p LEFT JOIN FETCH p.imgUris WHERE p.roomId = :roomId ORDER BY p.idx")
	List<Plan> findByRoomIdOrderByIndex(@Param("roomId") Long roomId);

	@Modifying
	@Transactional
	@Query(value = """
		UPDATE plan p1
		JOIN plan p2 ON p1.room_id = p2.room_id
		SET p1.idx = :idx2, p2.idx = :idx1
		WHERE p1.room_id = :roomId AND p1.idx = :idx1 AND p2.idx = :idx2
		""", nativeQuery = true)
	int swapIndexes(@Param("roomId") Long roomId, @Param("idx1") Long idx1, @Param("idx2") Long idx2);

	@Query("SELECT Max(p.idx) FROM Plan p")
	Long findMaxIdx();

	int deleteByIdx(Long idx);
}

