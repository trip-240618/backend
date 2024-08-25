package com.ll.trip.domain.trip.trip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.trip.entity.Bookmark;
import com.ll.trip.domain.trip.trip.entity.BookmarkId;

@Transactional
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	Optional<Bookmark> findById(BookmarkId id);

	@Query("UPDATE Bookmark b SET b.toggle = :toggle WHERE b.id = :id")
	int updateToggleById(BookmarkId id, boolean toggle);
}
