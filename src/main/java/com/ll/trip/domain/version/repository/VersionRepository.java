package com.ll.trip.domain.version.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.version.entity.Version;

public interface VersionRepository extends JpaRepository<Version, Long> {
	Optional<Version> findTopByOrderByCreateDateDesc();
}
