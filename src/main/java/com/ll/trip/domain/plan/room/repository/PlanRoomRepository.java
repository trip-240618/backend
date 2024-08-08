package com.ll.trip.domain.plan.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.plan.room.entity.Trip;

public interface PlanRoomRepository extends JpaRepository<Trip, Long> {
	boolean existsById(Long Id);
}
