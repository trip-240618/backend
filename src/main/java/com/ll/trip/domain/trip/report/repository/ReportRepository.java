package com.ll.trip.domain.trip.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.trip.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

}
