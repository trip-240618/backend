package com.ll.trip.domain.util.version.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ll.trip.domain.util.version.entity.Version;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {
	Optional<Version> findTopByOrderByCreateDateDesc();
}
