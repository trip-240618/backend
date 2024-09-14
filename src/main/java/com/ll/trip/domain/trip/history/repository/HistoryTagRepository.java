package com.ll.trip.domain.trip.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.trip.history.entity.HistoryTag;

public interface HistoryTagRepository extends JpaRepository<HistoryTag, Long> {

}
