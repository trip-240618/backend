package com.ll.trip.domain.trip.planJ.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto;
import com.ll.trip.domain.trip.planJ.entity.PlanJ;

public interface PlanJRepository extends JpaRepository<PlanJ, Long> {
	@Query("select max(p.orderByDate) from PlanJ p where p.trip.id = :tripId and p.dayAfterStart = :dayAfterStart")
	Integer findMaxOrder(Long tripId, int dayAfterStart);

	@Query("""
		select new com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto(
				p.id,
				p.dayAfterStart,
				p.orderByDate,
				p.startTime,
				p.writerUuid,
				p.title,
				p.memo,
				p.flightId,
				p.latitude,
				p.longitude
		) from PlanJ p
		where p.trip.id = :tripId and
		p.dayAfterStart = :day
		order by p.startTime asc, p.orderByDate asc
		""")
	List<PlanJInfoDto> findAllByTripIdAndDay(long tripId, int day);

	@Modifying
	@Query("""
		update PlanJ p
		set p.orderByDate = :orderByDate,
		p.startTime = :startTime,
		p.dayAfterStart = :dayAfterStart
		where p.id = :planId
		""")
	int updateStartTimeAndDayAfterStartAndOrderByPlanId(LocalTime startTime, int dayAfterStart, long planId, int orderByDate);

	@Modifying
	@Query("""
		update PlanJ p
		set p.orderByDate = :orderByDate,
		p.startTime = :startTime
		where p.id = :planId
		""")
	int updateStartTimeAndOrder(long planId, LocalTime startTime, int orderByDate);
}
