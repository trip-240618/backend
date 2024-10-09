package com.ll.trip.domain.trip.scrap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.scrap.dto.ScrapListDto;
import com.ll.trip.domain.trip.scrap.entity.Scrap;
import com.ll.trip.domain.trip.scrap.entity.ScrapBookmark;
import com.ll.trip.domain.trip.scrap.entity.ScrapImage;
import com.ll.trip.domain.trip.scrap.repository.ScrapBookmarkRepository;
import com.ll.trip.domain.trip.scrap.repository.ScrapImageRepository;
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
	private final ScrapImageRepository scrapImageRepository;
	private final EntityManager entityManager;

	@Transactional
	public Scrap createScrap(String uuid, long tripId, String title, String content, String color, boolean hasImage,
		List<String> photoList) {
		Trip tripRef = entityManager.getReference(Trip.class, tripId);
		String preview = createPreviewContent(content);
		Scrap scrap = scrapRepository.save(Scrap.builder()
			.writerUuid(uuid)
			.trip(tripRef)
			.preview(preview)
			.title(title)
			.content(content)
			.hasImage(hasImage)
			.color(color)
			.build());

		List<ScrapImage> imageList = buildScrapImages(tripRef, scrap, photoList);
		scrapImageRepository.saveAll(imageList);

		return scrap;
	}

	private List<ScrapImage> buildScrapImages(Trip tripRef, Scrap scrap, List<String> photoList) {
		List<ScrapImage> imageList = new ArrayList<>();
		for (String img : photoList) {
			imageList.add(
				ScrapImage.builder()
					.trip(tripRef)
					.scrap(scrap)
					.imgKey(img)
					.build()
			);
		}
		return imageList;
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

	public List<ScrapListDto> findAllByTripIdAndUserId(long tripId, long userId) {
		return scrapRepository.findListByTripId(tripId, userId);
	}

	public List<ScrapListDto> findAllBookmarkByTripIdAndUserId(long tripId, Long userId) {
		return scrapRepository.findBookmarkListByTripId(tripId, userId);
	}

	public boolean existByScrapIdAndUuid(long scrapId, String uuid) {
		return scrapRepository.existsByIdAndWriterUuid(scrapId, uuid);
	}

	@Transactional
	public Scrap modifyScrap(long scrapId, String title, String content, String color, boolean hasImage) {
		Scrap scrap = entityManager.getReference(Scrap.class, scrapId);
		scrap.setTitle(title);
		scrap.setContent(content);
		scrap.setPreview(createPreviewContent(content));
		scrap.setHasImage(hasImage);
		scrap.setColor(color);

		return scrapRepository.save(scrap);
	}

	@Transactional
	public void deleteById(long scrapId) {
		scrapRepository.deleteById(scrapId);
	}

	public Scrap findByIdWithScrapImage(long scrapId) {
		return scrapRepository.findByIdWithScrapImage(scrapId).orElseThrow(NoSuchElementException::new);
	}
}
