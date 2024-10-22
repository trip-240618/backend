package com.ll.trip.domain.trip.planJ.repository;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto;
import com.ll.trip.domain.trip.planJ.entity.PlanJ;

public interface PlanJRepository extends JpaRepository<PlanJ, Long> {
	@Query("select max(p.orderByDate) from PlanJ p where p.trip.id = :tripId")
	Integer findMaxOrder(Long tripId);

	@Query("""
		select new com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto(
		p.id, p.dayAfterStart, p.orderByDate, p.startTime, p.title, p.memo,
		p.place, p.latitude, p.longitude, p.locker
		) from PlanJ p
		where p.trip.id = :tripId and
		p.dayAfterStart = :day and
		p.locker = :locker
		order by p.startTime asc, p.orderByDate asc
		""")
	List<PlanJInfoDto> findAllPlanAByTripIdAndDay(long tripId, int day, boolean locker);

	@Query("""
		select new com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto(
		p.id, p.dayAfterStart, p.orderByDate, p.startTime, p.title, p.memo,
		p.place, p.latitude, p.longitude, p.locker
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

	@Modifying
	@Query("""
		update PlanJ p
		set p.dayAfterStart = p.dayAfterStart + :dayDiffer
		where p.trip.id = :tripId
		""")
	int updateDayByTripId(Long tripId, int dayDiffer);

	@Modifying
	@Query("""
		delete PlanJ p
		where p.trip.id = :tripId and (p.dayAfterStart < 1 or p.dayAfterStart > :duration)
		""")
	void deleteByTripIdAndDuration(Long tripId, int duration);

	void deleteByIdAndDayAfterStart(long id, Integer day);

	@Modifying
	@Query("UPDATE PlanJ p SET p.title = :title, p.memo = :memo, " +
		   "p.dayAfterStart = :dayAfterStart, p.startTime = :startTime, " +
		   "p.latitude = :latitude, p.longitude = :longitude, " +
		   "p.place = :place, p.orderByDate = :order, " +
		   "p.locker = :locker WHERE p.id = :planId")
	void updatePlan(long planId, String title, String memo, int dayAfterStart, LocalTime startTime, BigDecimal latitude, BigDecimal longitude, String place, Integer order, boolean locker);
}
