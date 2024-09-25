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
				p.latitude,
				p.longitude
		) from PlanJ p
		where p.trip.id = :tripId and
		p.dayAfterStart = :day and
		p.locker = :locker
		order by p.startTime asc, p.orderByDate asc
		""")
	List<PlanJInfoDto> findAllPlanAByTripIdAndDay(long tripId, int day, boolean locker);

	@Query("""
		select new com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto(
				p.id,
				p.dayAfterStart,
				p.orderByDate,
				p.startTime,
				p.writerUuid,
				p.title,
				p.memo,
				p.latitude,
				p.longitude
		) from PlanJ p
		where p.trip.id = :tripId and
		p.locker = :locker
		order by p.startTime asc, p.orderByDate asc
		""")
	List<PlanJInfoDto> findAllPlanBByTripIdAndDay(long tripId, boolean locker);

	@Modifying
	@Query("""
		update PlanJ p
		set p.orderByDate = :orderByDate,
		p.startTime = :startTime
		where p.id = :planId
		""")
	int updateStartTimeAndOrder(long planId, LocalTime startTime, int orderByDate);
}
