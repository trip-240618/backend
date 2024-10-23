package com.ll.trip.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

}
