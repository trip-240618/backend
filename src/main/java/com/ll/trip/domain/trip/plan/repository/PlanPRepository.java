package com.ll.trip.domain.trip.plan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.plan.dto.PlanPDeleteDto;
import com.ll.trip.domain.trip.plan.entity.PlanP;

public interface PlanPRepository extends JpaRepository<PlanP, Long> {
	@Query("select max(p.orderByDate) from PlanP p where p.trip.id = :tripId and p.dayAfterStart = :dayAfterStart")
	Integer findMaxIdx(Long tripId, int dayAfterStart);

	@Query("""
		SELECT new com.ll.trip.domain.trip.plan.dto.PlanPInfoDto(
			p.id,
			p.dayAfterStart,
			p.orderByDate,
			p.writerUuid,
			p.content,
			p.checkbox
		)
		FROM PlanP p
		WHERE p.trip.id = :tripId
		order by p.dayAfterStart asc, p.orderByDate asc
		""")
	List<PlanP> findAllByTripIdOrderByDayAfterStartAndOrderByDate(long tripId);

	Optional<PlanP> findPlanPById(long planId);

	@Query("""
			select new com.ll.trip.domain.trip.plan.dto.PlanPDeleteDto(
				p.trip.id,
				p.id,
				p.dayAfterStart,
				p.orderByDate
			)
			from PlanP p
			where p.id = :planId
		"""
	)
	Optional<PlanPDeleteDto> findPlanPDeleteDtoByPlanId(long planId);

	@Query("""
					update PlanP p
					set p.orderByDate = p.orderByDate + 1
					where p.trip.id = :tripId and
					p.dayAfterStart = :dayAfterStart and
					p.orderByDate > :orderByDate
		"""
	)
	int subtractOrder(long tripId, int dayAfterStart, int orderByDate);
}
