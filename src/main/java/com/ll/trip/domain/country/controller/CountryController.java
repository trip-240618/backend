package com.ll.trip.domain.country.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.country.entity.Country;
import com.ll.trip.domain.country.service.CountryService;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.domain.user.user.dto.VisitedCountryDto;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/country")
@Tag(name = "Country")
public class CountryController {
	private final CountryService countryService;
	private final TripService tripService;

	@GetMapping("/search")
	@Operation(summary = "나라 검색")
	@ApiResponse(responseCode = "200", description = "db에서 country필드에 있는 나라이름으로 검색")
	public ResponseEntity<Country> searchCountry(
		@RequestParam String countryName
	){
		log.info("검색 국가: " + countryName);
		Country response = countryService.findCountryByName(countryName);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/search/autocomplete")
	@Operation(summary = "나라 검색")
	@ApiResponse(responseCode = "200", description = "db에서 country필드에 있는 나라이름으로 검색")
	public ResponseEntity<List<String>> autocompleteCountry(
		@RequestParam String keyword
	){
		List<String> response = countryService.findAllCountryNameLike(keyword);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/visited")
	@Operation(summary = "방문한 나라")
	@ApiResponse(responseCode = "200", description = "방문한 나라")
	public ResponseEntity<List<VisitedCountryDto>> showVisitedCountry(
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		List<VisitedCountryDto> response = tripService.findVisitedCountry(securityUser.getId());

		return ResponseEntity.ok(response);
	}
}
