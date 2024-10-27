package com.ll.trip.domain.trip.planP.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.planP.dto.PlanPInfoDto;
import com.ll.trip.domain.trip.planP.entity.PlanP;

public interface PlanPRepository extends JpaRepository<PlanP, Long> {
	@Query("select max(p.orderByDate) from PlanP p where p.trip.id = :tripId and p.dayAfterStart = :day and p.locker = :locker")
	Integer findMaxOrder(Long tripId, Integer day, boolean locker);

	@Query("""
		SELECT new com.ll.trip.domain.trip.planP.dto.PlanPInfoDto(
			p.id,
			p.dayAfterStart,
			p.orderByDate,
			p.content,
			p.checkbox,
			p.locker
		)
		FROM PlanP p
		WHERE p.trip.id = :tripId and
		p.locker = true
		order by p.dayAfterStart asc, p.orderByDate asc
		""")
	List<PlanPInfoDto> findAllLockerByTripId(long tripId);

	@Query("""
		SELECT new com.ll.trip.domain.trip.planP.dto.PlanPInfoDto(
			p.id,
			p.dayAfterStart,
			p.orderByDate,
			p.content,
			p.checkbox,
			p.locker
		)
		FROM PlanP p
		WHERE p.trip.id = :tripId and
		p.locker = false
		and p.dayAfterStart >= :dayFrom
		and p.dayAfterStart <= :dayTo
		order by p.dayAfterStart asc, p.orderByDate asc
		""")
	List<PlanPInfoDto> findAllByTripId(long tripId, int dayFrom, int dayTo);

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
		set p.orderByDate = :order
		where p.id = :planId
		""")
	int updateOrderByPlanId(long planId, int order);

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

	@Modifying
	@Query("""
		update PlanP p
		set p.content = :content,
		p.checkbox = :checkbox
		where p.id = :planId
		""")
	void modifyPlanP(long planId, String content, boolean checkbox);

	@Modifying
	@Query("""
		update PlanP p
		set p.dayAfterStart = :day,
		p.orderByDate = :order,
		p.locker = :locker
		where p.id = :planId
		""")
	void moveLocker(long planId, int day, int order, boolean locker);
}
