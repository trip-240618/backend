package com.ll.trip.domain.trip.scrap.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.scrap.entity.ScrapBookmark;

public interface ScrapBookmarkRepository extends JpaRepository<ScrapBookmark, Long> {
	@Modifying
	@Query("""
		UPDATE ScrapBookmark b
		SET b.toggle = CASE WHEN b.toggle = true THEN false ELSE true END
		WHERE b.user.id = :userId and
		b.scrap.id = :scrapId
		""")
	int toggleScrapBookmark(long userId, long scrapId);

	@Query("""
		select b.toggle
		from ScrapBookmark b
		where b.user.id = :userId and
		b.scrap.id = :scrapId
		""")
	Optional<Boolean> getIsToggleByUserIdAndScrapId(long userId, long scrapId);
}
