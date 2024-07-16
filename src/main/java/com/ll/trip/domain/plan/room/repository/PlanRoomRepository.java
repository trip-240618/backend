package com.ll.trip.domain.plan.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.plan.room.entity.PlanRoom;

public interface PlanRoomRepository extends JpaRepository<PlanRoom, Long> {
	boolean existsById(Long Id);
}
