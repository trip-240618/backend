package com.ll.trip.domain.country.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.country.entity.Country;
import com.ll.trip.domain.country.service.CountryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CountryController {
	private final CountryService countryService;

	@GetMapping("/country/search")
	@Operation(summary = "나라 검색")
	@ApiResponse(responseCode = "200", description = "db에서 country필드에 있는 나라이름으로 검색", content = {
		@Content(mediaType = "application/json",
			schema = @Schema(implementation = Country.class))})
	public ResponseEntity<?> searchCountry(
		@RequestParam String countryName
	){
		log.info("검색 국가: " + countryName);
		Country response;
		try {
			 response = countryService.findCountryByName(countryName);
			 log.info(response.getCountryCode());
		} catch (NullPointerException n) {
			return ResponseEntity.badRequest().body("국가를 찾을 수 없습니다.");
		}
		return ResponseEntity.ok(response);
	}

}
