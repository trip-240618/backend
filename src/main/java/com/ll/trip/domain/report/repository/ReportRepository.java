package com.ll.trip.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

	@Modifying
	@Query("""
		update Report r
		set r.complete = :complete
		where r.id = :reportId
		""")
	void updateComplete(long reportId, boolean complete);
}
