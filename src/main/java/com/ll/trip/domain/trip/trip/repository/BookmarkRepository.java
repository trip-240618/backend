package com.ll.trip.domain.trip.trip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.trip.entity.Bookmark;

@Transactional
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	@Modifying
	@Query("""
		UPDATE Bookmark b
		SET b.toggle = CASE WHEN b.toggle = true THEN false ELSE true END
		WHERE b.user.id = :userId and
		b.trip.id = :tripId
		""")
	int toggleTripBookmarkByTripIdAndUserId(long userId, long tripId);

	@Query("""
		select b.toggle
		from Bookmark b
		where b.user.id = :userId and
		b.trip.id = :tripId
		""")
	Optional<Boolean> getIsToggleByUserIdAndTripId(long userId, long tripId);
}
