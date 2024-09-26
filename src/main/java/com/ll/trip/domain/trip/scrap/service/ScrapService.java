package com.ll.trip.domain.trip.scrap.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.scrap.entity.Scrap;
import com.ll.trip.domain.trip.scrap.entity.ScrapBookmark;
import com.ll.trip.domain.trip.scrap.repository.ScrapBookmarkRepository;
import com.ll.trip.domain.trip.scrap.repository.ScrapRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.user.user.entity.UserEntity;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapService {
	private final ScrapRepository scrapRepository;
	private final ScrapBookmarkRepository scrapBookmarkRepository;
	private final EntityManager entityManager;

	@Transactional
	public Scrap createScrap(long userId, Trip trip, String title, String content, String color, boolean hasImage) {
		String preview = createPreviewContent(content);

		Scrap scrap = Scrap.builder()
			.user(entityManager.getReference(UserEntity.class, userId))
			.trip(trip)
			.previewContent(preview)
			.title(title)
			.content(content)
			.hasImage(hasImage)
			.color(color)
			.build();

		return scrapRepository.save(scrap);
	}

	private String createPreviewContent(String content) {
		StringBuilder sb = new StringBuilder();
		String[] parts = content.split("\\{\"insert\":");

		int l = 0;
		for (String part : parts) {
			if (part.startsWith("{\"image\""))
				continue;
			int text = part.length() - 6;
			if (l + text > 50) {
				text = 50 - l + 2;

				sb.append("\\{\"insert\":").append(part, 0, text).append("}");
				break;
			}
			l += text;
			sb.append("\\{\"insert\":").append(part);
		}

		String preview = sb.toString();
		log.info("preview: " + preview);
		return preview;
	}

	@Transactional
	public int toggleScrapBookmark(long userId, long scrapId) {
		return scrapBookmarkRepository.toggleScrapBookmark(userId, scrapId);
	}

	@Transactional
	public void createScrapBookmark(long userId, long scrapId, long tripId) {
		UserEntity userReference = entityManager.getReference(UserEntity.class, userId);
		Scrap scrapReference = entityManager.getReference(Scrap.class, scrapId);
		Trip trip = entityManager.getReference(Trip.class, tripId);

		ScrapBookmark bookmark = ScrapBookmark.builder()
			.user(userReference)
			.scrap(scrapReference)
			.toggle(true)
			.trip(trip)
			.build();

		scrapBookmarkRepository.save(bookmark);
	}

	public boolean getIsToggleByUserIdAndScrapId(long userId, long scrapId) {
		return scrapBookmarkRepository.getIsToggleByUserIdAndScrapId(userId, scrapId)
			.orElseThrow(NullPointerException::new);
	}
}
