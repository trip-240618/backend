package com.ll.trip.healthCheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.healthCheck.entity.TestEntity;

public interface TestRepository extends JpaRepository<TestEntity, Long> {

}
