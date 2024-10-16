package com.ll.trip.domain.trip.planP.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.planP.dto.PlanPInfoDto;
import com.ll.trip.domain.trip.planP.entity.PlanP;

public interface PlanPRepository extends JpaRepository<PlanP, Long> {
	@Query("select max(p.orderByDate) from PlanP p where p.trip.id = :tripId and p.dayAfterStart = :day")
	Integer findMaxOrder(Long tripId, Integer day);

	@Query("""
		SELECT new com.ll.trip.domain.trip.planP.dto.PlanPInfoDto(
			p.id,
			p.dayAfterStart,
			p.orderByDate,
			p.writerUuid,
			p.content,
			p.checkbox
		)
		FROM PlanP p
		WHERE p.trip.id = :tripId and
		p.locker = :locker
		order by p.dayAfterStart asc, p.orderByDate asc
		""")
	List<PlanPInfoDto> findAllByTripIdOrderByDayAfterStartAndOrderByDate(long tripId, boolean locker);

	@Modifying
	@Query("""
		update PlanP p
		set p.checkbox = :checkbox
		where p.id = :planId
		""")
	int updateCheckBoxByPlanId(long planId, boolean checkbox);

	Optional<PlanP> findPlanPById(long planId);


	@Modifying
	@Query("""
		update PlanP p
		set p.dayAfterStart = :dayTo,
		p.orderByDate = :orderTo
		where p.id = :planId
		""")
	int updateDayOrderById(long planId, int dayTo, int orderTo);

	@Modifying
	@Query("""
		update PlanP p
		set p.orderByDate = p.orderByDate - 1
		where p.trip.id = :tripId and
		p.dayAfterStart = :day and
		p.orderByDate >= :from and
		p.orderByDate <= :to
		""")
	int reduceOrderFromToByTripIdAndDay(long tripId, int day, int from, int to);

	@Modifying
	@Query("""
		update PlanP p
		set p.orderByDate = p.orderByDate + 1
		where p.trip.id = :tripId and
		p.dayAfterStart = :day and
		p.orderByDate >= :from and
		p.orderByDate <= :to
		""")
	int increaseOrderFromToByTripIdAndDay(long tripId, int day, int from, int to);

	@Modifying
	@Query("""
		update PlanP p
		set p.orderByDate = p.orderByDate + 1
		where p.trip.id = :tripId and
		p.dayAfterStart = :day and
		p.orderByDate >= :from
		""")
	int increaseOrderFromByTripIdAndDay(long tripId, int day, int from);


	@Modifying
	@Query("""
		update PlanP p
		set p.orderByDate = :order,
		p.dayAfterStart = :dayTo,
		p.locker = :locker
		where p.id = :planId
		""")
	int updatePlanPDayAndLockerByPlanId(long planId, Integer dayTo, int order, boolean locker);

	void deleteByIdAndDayAfterStart(long planId, int day);
}
