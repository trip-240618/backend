package com.ll.trip.domain.trip.scrap.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.scrap.entity.Scrap;
import com.ll.trip.domain.trip.scrap.repository.ScrapRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.user.user.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapService {
	private ScrapRepository scrapRepository;

	public Scrap createScrap(UserEntity user, Trip trip, String title, String content, String color, boolean hasImage) {
		Scrap scrap = Scrap.builder()
			.user(user)
			.trip(trip)
			.title(title)
			.content(content)
			.hasImage(hasImage)
			.color(color)
			.build();

		return scrapRepository.save(scrap);
	}
}
