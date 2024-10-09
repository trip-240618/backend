package com.ll.trip.domain.trip.scrap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.scrap.entity.ScrapImage;

public interface ScrapImageRepository extends JpaRepository<ScrapImage, Long> {
	@Query("""
		select i.imgKey
		from ScrapImage i where i.scrap.id = :scrapId
		""")
	List<String> findAllImageKeyByScrapId(long scrapId);

	@Query("""
		select i.imgKey
		from ScrapImage i where i.trip.id = :tripId
		""")
	List<String> findAllImageKeyByTripId(long tripId);
}
