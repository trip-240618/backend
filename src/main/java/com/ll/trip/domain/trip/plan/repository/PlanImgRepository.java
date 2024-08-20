package com.ll.trip.domain.trip.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.trip.plan.entity.PlanImage;

public interface PlanImgRepository extends JpaRepository<PlanImage, Long> {
	public void deleteByUri(String uri);
}
