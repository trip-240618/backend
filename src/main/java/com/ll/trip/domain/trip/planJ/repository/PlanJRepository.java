package com.ll.trip.domain.trip.planJ.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.planJ.dto.PlanJDeleteDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto;
import com.ll.trip.domain.trip.planJ.entity.PlanJ;

public interface PlanJRepository extends JpaRepository<PlanJ, Long> {

	@Query(value = """
		    WITH sorted_plans AS (
		        SELECT p.*
		        FROM plan_j p
		        WHERE p.trip_id = :tripId AND p.day_after_start = :dayAfterStart
		        ORDER BY p.start_time ASC
		    )
		    SELECT 
		        MAX(p.order_by_date),  -- 최대 order_by_date 값을 가져옴
		        (SELECT sp.order_by_date 
		         FROM sorted_plans sp 
		         WHERE sp.start_time > :startTime 
		         LIMIT 1)  -- startTime보다 큰 첫 번째 order_by_date 값을 가져옴
		    FROM sorted_plans p
		""", nativeQuery = true)
	Object[] findOrderByDayAndStartTime(long tripId, int dayAfterStart, LocalTime startTime);

	@Modifying
	@Query("""
		update PlanJ p
		set p.orderByDate = p.orderByDate + 1
		where p.trip.id = :tripId and
		p.dayAfterStart = :day and
		p.orderByDate >= :order
		""")
	int increaseOrderWhereBiggerThanOrder(long tripId, int day, int order);

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
		order by p.orderByDate
		""")
	List<PlanJInfoDto> findAllByTripIdAndDay(long tripId, int day);

	@Modifying
	@Query("""
		update PlanJ p
		set p.orderByDate = p.orderByDate - 1
		where p.trip.id = :tripId and
		p.dayAfterStart = :day and
		p.orderByDate >= :from and
		p.orderByDate <= :to
		""")
	int reduceOrderFromToByTripIdAndDay(long tripId, int day, int from, int to);

	@Modifying
	@Query("""
		update PlanJ p
		set p.orderByDate = p.orderByDate + 1
		where p.trip.id = :tripId and
		p.dayAfterStart = :day and
		p.orderByDate >= :from and
		p.orderByDate <= :to
		""")
	int increaseOrderFromToByTripIdAndDay(long tripId, int day, int from, int to);

	@Query("""
			select new com.ll.trip.domain.trip.planJ.dto.PlanJDeleteDto(
				p.trip.id,
				p.id,
				p.dayAfterStart,
				p.orderByDate
			)
			from PlanJ p
			where p.id = :planId
		"""
	)
	Optional<PlanJDeleteDto> findPlanJDeleteDtoByPlanId(Long planId);

	@Modifying
	@Query("""
					update PlanJ p
					set p.orderByDate = p.orderByDate + 1
					where p.trip.id = :tripId and
					p.dayAfterStart = :dayAfterStart and
					p.orderByDate > :orderByDate
		"""
	)
	int reduceOrderBiggerThanOrder(long tripId, int dayAfterStart, int orderByDate);
}
