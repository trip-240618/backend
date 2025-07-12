package com.ll.trip.domain.report.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.history.history.dto.HistoryDto;
import com.ll.trip.domain.history.history.dto.HistoryReplyDto;
import com.ll.trip.domain.history.history.service.HistoryService;
import com.ll.trip.domain.report.entity.Report;
import com.ll.trip.domain.report.service.ReportService;
import com.ll.trip.domain.trip.scrap.dto.ScrapDetailDto;
import com.ll.trip.domain.trip.scrap.service.ScrapService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@Tag(name = "Report")
public class ReportController {
	private final ReportService reportService;
	private final HistoryService historyService;
	private final ScrapService scrapService;

	@PostMapping("/create")
	@Operation(summary = "신고 생성")
	public ResponseEntity<String> createReport(
		@RequestParam @Schema(example = "reply") String type,
		@RequestParam @Schema(example = "1") long tripId,
		@RequestParam(required = false) @Schema(description = "reply는 historyId도 같이 줘야댐") Long historyId,
		@RequestParam @Schema(example = "1") long typeId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		long userId = securityUser.getId();

		switch (type) {
			case "history":
				HistoryDto historyDto = historyService.findByHistoryId(typeId, userId);
				reportService.createHistoryReport(type, typeId, tripId, userId, historyDto);
				break;
			case "reply":
				HistoryReplyDto replyDto = historyService.findReplyById(typeId);
				reportService.createReplyReport(type, historyId, typeId, tripId, userId, replyDto);
				break;
			case "scrap":
				ScrapDetailDto scrapDto = scrapService.findByIdWithScrapImage(typeId, userId);
				reportService.createScrapReport(type, typeId, tripId, userId, scrapDto);
				break;
		}

		return ResponseEntity.ok("created");
	}

	@GetMapping("/list")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "신고 목록")
	public ResponseEntity<Page<Report>> showReportList(
		@RequestParam int page,
		@RequestParam int size
	) {
		Page<Report> response = reportService.showReportList(page, size);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/complete")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "신고 목록")
	public ResponseEntity<String> showReportList(
		@RequestParam long reportId,
		@RequestParam @Schema(description = "변경 후 상태") boolean complete
	) {
		reportService.updateComplete(reportId, complete);
		return ResponseEntity.ok("complete");
	}

}
