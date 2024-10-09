package com.ll.trip.domain.trip.scrap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.scrap.dto.ScrapListDto;
import com.ll.trip.domain.trip.scrap.entity.Scrap;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

	@Query("""
			select new com.ll.trip.domain.trip.scrap.dto.ScrapListDto(
			s.id, s.writerUuid, s.title, s.preview, s.hasImage, s.color,
		 	COALESCE(b.toggle, false), s.createDate
			) from Scrap s
			left join ScrapBookmark b
			on b.scrap.id = s.id and b.user.id = :userId and s.trip.id = :tripId
		""")
	List<ScrapListDto> findListByTripId(long tripId, long userId);

	@Query("""
			select new com.ll.trip.domain.trip.scrap.dto.ScrapListDto(
			s.id, s.writerUuid, s.title, s.preview, s.hasImage, s.color,
		 	b.toggle, s.createDate
			) from ScrapBookmark b
			inner join Scrap s on b.scrap.id = s.id
			and b.trip.id = :tripId and b.user.id = :userId and b.toggle = true
		""")
	List<ScrapListDto> findBookmarkListByTripId(long tripId, long userId);

	boolean existsByIdAndWriterUuid(long scrapId, String uuid);
}
