package com.ll.trip.domain.trip.report.service;

import org.springframework.stereotype.Service;

import com.ll.trip.domain.trip.report.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
	private final ReportRepository reportRepository;

}
