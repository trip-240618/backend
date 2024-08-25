package com.ll.trip.domain.trip.trip.repository;

import java.time.LocalDate;
import java.util.List;

import com.ll.trip.domain.trip.trip.dto.TripInfoDto;

public interface TripRepositoryCustom {
	List<TripInfoDto> findTripInfosWithDynamicSort(Long userId, LocalDate today, String sortField,
		String sortDirection, String type);

	List<TripInfoDto> findBookmarkTripInfosWithDynamicSort(Long userId, String sortField,
		String sortDirection, String type);
}
