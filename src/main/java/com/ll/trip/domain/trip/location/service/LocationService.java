package com.ll.trip.domain.trip.location.service;

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

import com.ll.trip.domain.trip.location.dto.AutoCompleteRequestDto;
import com.ll.trip.domain.trip.location.dto.AutoCompleteResponseDto;
import com.ll.trip.domain.trip.location.dto.AutocompleteResponse;
import com.ll.trip.domain.trip.location.dto.LocationDto;
import com.ll.trip.domain.trip.location.dto.TextQueryLocationResponseDto;
import com.ll.trip.domain.trip.location.dto.PlaceDetailResponse;
import com.ll.trip.domain.trip.location.dto.TextSearchDto;

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

	public LocationDto getPlaceLocation(String text) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Goog-Api-Key", API_KEY);
		headers.set("X-Goog-FieldMask", "places.location");

		HttpEntity<TextSearchDto> requestEntity = new HttpEntity<>(new TextSearchDto(text), headers);

		ResponseEntity<TextQueryLocationResponseDto> responseEntity = restTemplate.exchange(
			"https://places.googleapis.com/v1/places:searchText",
			HttpMethod.POST,
			requestEntity,
			TextQueryLocationResponseDto.class
		);

		LocationDto location = null;

		if(responseEntity.getBody() != null && responseEntity.getBody().getPlaces().length > 0) {
			location = responseEntity.getBody().getPlaces()[0].getLocation();
		}

		return location;
	}
}
