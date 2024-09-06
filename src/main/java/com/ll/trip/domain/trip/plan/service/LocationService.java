package com.ll.trip.domain.trip.plan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ll.trip.domain.trip.plan.dto.AutoCompleteRequestDto;
import com.ll.trip.domain.trip.plan.dto.AutoCompleteResponseDto;
import com.ll.trip.domain.trip.plan.dto.AutocompleteResponse;
import com.ll.trip.domain.trip.plan.dto.PlaceDetailResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

	@Value(value = "${data.google-maps.api-key}")
	private String API_KEY;

	private final RestTemplate restTemplate;

	public List<AutoCompleteResponseDto> getAutoComplete(AutoCompleteRequestDto requestDto) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Goog-Api-Key", API_KEY);

		// 요청 엔티티 생성
		HttpEntity<AutoCompleteRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

		// API 호출
		ResponseEntity<AutocompleteResponse> responseEntity = restTemplate.exchange(
			"https://places.googleapis.com/v1/places:autocomplete",
			HttpMethod.POST,
			requestEntity,
			AutocompleteResponse.class
		);

		List<AutoCompleteResponseDto> response = new ArrayList<>();
		if (responseEntity.getBody() != null && responseEntity.getBody().getSuggestions() != null) {
			response = responseEntity.getBody().getSuggestions().stream()
				.map(suggestion -> new AutoCompleteResponseDto(
					suggestion.getPlacePrediction().getPlaceId(),
					suggestion.getPlacePrediction().getText().getText(),
					suggestion.getPlacePrediction().getStructuredFormat().getSecondaryText().getText()
				))
				.collect(Collectors.toList());
		}

		return response;
	}

	public PlaceDetailResponse getPlaceDetail(String placeId) {
		String url = "https://places.googleapis.com/v1/places/" + placeId +
					 "?fields=location,formattedAddress,displayName&languageCode=ko&key=" + API_KEY;

		ResponseEntity<PlaceDetailResponse> responseEntity = restTemplate.getForEntity(url, PlaceDetailResponse.class);

		return responseEntity.getBody();
	}
}
