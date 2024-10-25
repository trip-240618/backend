package com.ll.trip.domain.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.trip.domain.history.history.dto.HistoryDto;
import com.ll.trip.domain.history.history.dto.HistoryReplyDto;
import com.ll.trip.domain.report.entity.Report;
import com.ll.trip.domain.report.repository.ReportRepository;
import com.ll.trip.domain.trip.scrap.dto.ScrapDetailDto;
import com.ll.trip.global.handler.exception.ServerException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
	private final ReportRepository reportRepository;

	@Transactional
	public void createHistoryReport(String type, long typeId, long tripId, long userId, HistoryDto historyDto) {
		String jsonData = parseToString(historyDto);
		String deleteUrl = "/trip/" + tripId + "/history/delete/" + typeId;
		buildAndSaveReport(type, jsonData, deleteUrl, userId);
	}

	public void createReplyReport(String type, long historyId, long typeId, long tripId, long userId,
		HistoryReplyDto replyDto) {
		String jsonData = parseToString(replyDto);
		String deleteUrl = "/trip/" + tripId + "/history/" + historyId + "/reply/delete?replyId=" + typeId;
		buildAndSaveReport(type, jsonData, deleteUrl, userId);
	}

	public void createScrapReport(String type, long typeId, long tripId, long userId, ScrapDetailDto scrapDto) {
		String jsonData = parseToString(scrapDto);
		String deleteUrl = "/trip/" + tripId + "/scrap/delete?scrapId=" + typeId;
		buildAndSaveReport(type, jsonData, deleteUrl, userId);
	}

	private String parseToString(Object data) {
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = null;
		try {
			jsonString = objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			throw new ServerException("object parsing 실패");
		}

		return jsonString;
	}

	private void buildAndSaveReport(String type, String jsonData, String deleteUrl, long userId) {
		Report report = Report.builder()
			.type(type)
			.jsonData(jsonData)
			.deleteUrl(deleteUrl)
			.userId(userId)
			.complete(false)
			.build();
		reportRepository.save(report);
	}

	public Page<Report> showReportList(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
		return reportRepository.findAll(pageable);
	}

	@Transactional
	public void updateComplete(long reportId, boolean complete) {
		reportRepository.updateComplete(reportId, complete);
	}

}
