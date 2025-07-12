package com.ll.trip.domain.mypage.faq.controller;

import com.ll.trip.domain.mypage.faq.dto.FaqCreateDto;
import com.ll.trip.domain.mypage.faq.dto.FaqListDto;
import com.ll.trip.domain.mypage.faq.entity.Faq;
import com.ll.trip.domain.mypage.faq.service.FaqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/faq")
@Tag(name = "FAQ")
public class FaqController {
	private final FaqService faqService;

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/create")
	@Operation(summary = "FAQ 생성")
	@ApiResponse(responseCode = "200", description = "FAQ 생성", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = Faq.class))})
	public ResponseEntity<?> createFaq(
		@RequestBody FaqCreateDto faqCreateDto
	) {
		Faq response = faqService.createFaq(faqCreateDto);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/modify/{faqId}")
	@Operation(summary = "FAQ 수정")
	@ApiResponse(responseCode = "200", description = "FAQ 생성", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = Faq.class))})
	public ResponseEntity<?> modifyFaq(
		@PathVariable @Parameter(description = "Faq id", example = "1", in = ParameterIn.PATH) long faqId,
		@RequestBody FaqCreateDto faqCreateDto
	) {
		Faq response = faqService.modifyFaq(faqId, faqCreateDto);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/delete/{faqId}")
	@Operation(summary = "FAQ 삭제")
	@ApiResponse(responseCode = "200", description = "FAQ 생성", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = FaqCreateDto.class))})
	public ResponseEntity<?> deleteFaq(
		@PathVariable @Parameter(description = "Faq id", example = "1", in = ParameterIn.PATH) long faqId,
		@RequestBody FaqCreateDto faqCreateDto
	) {
		faqService.deleteFaq(faqId);
		return ResponseEntity.ok("deleted");
	}

	@GetMapping("/list")
	@Operation(summary = "FAQ 목록")
	@ApiResponse(responseCode = "200", description = "FAQ 목록", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FaqListDto.class)))})
	public ResponseEntity<?> showFaqList(
		@RequestParam(required = false) String type
	) {
		List<FaqListDto> response = faqService.findFaqByType(type);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/detail/{faqId}")
	@Operation(summary = "FAQ 상세")
	@ApiResponse(responseCode = "200", description = "FAQ 상세", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = Faq.class))})
	public ResponseEntity<?> showFaqDetail(
		@PathVariable @Parameter(description = "Faq id", example = "1", in = ParameterIn.PATH) long faqId
	) {
		Faq response = faqService.findFaqById(faqId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	@Operation(summary = "FAQ 검색")
	@ApiResponse(responseCode = "200", description = "FAQ 검색", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FaqListDto.class)))})
	public ResponseEntity<?> searchFaq(
		@RequestParam @NotBlank String text
	) {
		List<FaqListDto> response = faqService.findFaqByText(text);
		return ResponseEntity.ok(response);
	}
}
