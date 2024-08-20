package com.ll.trip.domain.trip.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.trip.trip.entity.Trip;

public interface PlanRoomRepository extends JpaRepository<Trip, Long> {
	boolean existsById(Long Id);
}
