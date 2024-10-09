package com.ll.trip.domain.trip.scrap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.scrap.dto.ScrapDetailServiceDto;
import com.ll.trip.domain.trip.scrap.dto.ScrapListDto;
import com.ll.trip.domain.trip.scrap.entity.Scrap;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

	@Query("""
			select new com.ll.trip.domain.trip.scrap.dto.ScrapListDto(
			s.id, u.uuid, u.nickname, s.title, s.preview, s.hasImage, s.color,
		 	COALESCE(b.toggle, false), s.createDate
			) from Scrap s
			inner join s.user u on s.trip.id = :tripId
			left join ScrapBookmark b on b.scrap.id = s.id and b.user.id = :userId
		""")
	List<ScrapListDto> findListByTripId(long tripId, long userId);

	@Query("""
			select new com.ll.trip.domain.trip.scrap.dto.ScrapListDto(
			s.id, u.uuid, u.nickname, s.title, s.preview, s.hasImage, s.color,
		 	b.toggle, s.createDate
			) from ScrapBookmark b
			inner join b.scrap s
			on b.trip.id = :tripId and b.user.id = :userId and b.toggle = true
			left join s.user u
		""")
	List<ScrapListDto> findBookmarkListByTripId(long tripId, long userId);

	boolean existsByIdAndUser_Id(long scrapId, long userId);

	@Query("""
		select new com.ll.trip.domain.trip.scrap.dto.ScrapDetailServiceDto(
		s.id, u.uuid, u.nickname, s.title, s.content, s.hasImage, s.color,
		COALESCE(b.toggle, false), s.createDate, i.id, i.imgKey
		)
		from Scrap s
		inner join s.user u on s.id = :scrapId
		left join s.scrapImageList i
		left join ScrapBookmark b on b.user.id = :userId and b.scrap.id = :scrapId
		""")
	List<ScrapDetailServiceDto> findDetailDtoByScrapIdAndUserId(long scrapId, long userId);
}
