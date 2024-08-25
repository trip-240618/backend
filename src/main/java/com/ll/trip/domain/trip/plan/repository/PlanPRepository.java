package com.ll.trip.domain.trip.plan.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.trip.plan.entity.PlanP;

public interface PlanPRepository extends JpaRepository<PlanP, Long> {
	@Query("select max(p.orderByDate) from PlanP p where p.trip.id = :tripId and p.startDate = :startDate")
	Integer findMaxIdx(Long tripId, LocalDate startDate);
}
