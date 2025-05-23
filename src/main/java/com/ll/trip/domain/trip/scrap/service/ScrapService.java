package com.ll.trip.domain.trip.scrap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.file.file.service.AwsAuthService;
import com.ll.trip.domain.trip.scrap.dto.ScrapDetailDto;
import com.ll.trip.domain.trip.scrap.dto.ScrapDetailServiceDto;
import com.ll.trip.domain.trip.scrap.dto.ScrapImageDto;
import com.ll.trip.domain.trip.scrap.dto.ScrapListDto;
import com.ll.trip.domain.trip.scrap.entity.Scrap;
import com.ll.trip.domain.trip.scrap.entity.ScrapBookmark;
import com.ll.trip.domain.trip.scrap.entity.ScrapImage;
import com.ll.trip.domain.trip.scrap.repository.ScrapBookmarkRepository;
import com.ll.trip.domain.trip.scrap.repository.ScrapImageRepository;
import com.ll.trip.domain.trip.scrap.repository.ScrapRepository;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.global.handler.exception.NoSuchDataException;
import com.ll.trip.global.handler.exception.PermissionDeniedException;

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
	private final AwsAuthService awsAuthService;

	@Transactional
	public Scrap createScrap(long userId, long tripId, String title, String content, String color, boolean hasImage,
		List<String> photoList) {
		Trip tripRef = entityManager.getReference(Trip.class, tripId);
		String preview = parseToPreviewContent(content);
		Scrap scrap = scrapRepository.save(Scrap.builder()
			.user(entityManager.getReference(UserEntity.class, userId))
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

	private List<ScrapImage> buildScrapImages(Trip tripRef, Scrap scrapRef, List<String> photoList) {
		List<ScrapImage> imageList = new ArrayList<>();
		for (String img : photoList) {
			imageList.add(
				ScrapImage.builder()
					.trip(tripRef)
					.scrap(scrapRef)
					.imgKey(img)
					.build()
			);
		}
		return imageList;
	}

	private String parseToPreviewContent(String content) {
		StringBuilder sb = new StringBuilder();
		String[] parts = content.split("\\{\"insert\":");

		int l = 0;
		sb.append(parts[0]);
		sb.append("{\"insert\":\"");
		for (int i = 1; i < parts.length; i++) {
			String part = parts[i];
			if (part.startsWith("{\"image\""))
				continue;
			String[] partA = part.split(",\"attributes\"");
			part = partA[0];
			int text;
			int start = 1;
			if (partA.length > 1) {
				start = 1;
				text = part.length() - 1;
			} else {
				text = part.length() - 5;
			}

			if (l + text > 50) {
				text = 50 - l + 2;
				sb.append(part, start, text);
				if (part.charAt(text - 1) == '\\' && part.length() > text) {
					sb.append(part.charAt(text));
				}
				break;
			}
			l += text;
			sb.append(part, start, text);
		}
		sb.append("\\n\"}]");

		return sb.toString();
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

	public void checkIsWriterOfScrap(long scrapId, long userId) {
		if (!scrapRepository.existsByIdAndUser_Id(scrapId, userId)) {
			log.info("user: " + userId + " is not writer of scrap: " + scrapId);
			throw new PermissionDeniedException("user is not writer of scrap");
		}
	}

	@Transactional
	public void modifyScrap(long tripId, long scrapId, String title, String content, String color, boolean hasImage,
		List<ScrapImageDto> photoList) {
		scrapRepository.updateScrapFields(scrapId, title, content, parseToPreviewContent(content), hasImage, color);

		List<ScrapImage> imageList = scrapImageRepository.findByScrap_Id(scrapId);

		Map<Long, ScrapImage> idMap = imageList.stream()
			.collect(Collectors.toMap(
				ScrapImage::getId,    // 키: id
				image -> image
			));

		for (ScrapImageDto dto : photoList) {
			long id = dto.getId();
			String url = idMap.computeIfAbsent(id, key -> {
				return scrapImageRepository.saveAll(
					buildScrapImages(
						entityManager.getReference(Trip.class, tripId),
						entityManager.getReference(Scrap.class, scrapId),
						List.of(dto.getImageUrl()))
					).get(0);
			}).getImgKey();
			idMap.remove(id);
			if(!url.equals(dto.getImageUrl())) scrapImageRepository.updateScrapImageById(id, dto.getImageUrl());
		}

		for (ScrapImage s : idMap.values()) {
			scrapImageRepository.deleteById(s.getId());
		}
	}

	@Transactional
	public void deleteById(long scrapId) {
		awsAuthService.deleteImagesByScrapId(scrapId);
		scrapRepository.deleteById(scrapId);
	}

	public ScrapDetailDto findByIdWithScrapImage(long scrapId, long userId) {
		List<ScrapDetailServiceDto> dtos = scrapRepository.findDetailDtoByScrapIdAndUserId(scrapId, userId);
		if (dtos == null)
			throw new NoSuchDataException("scrap을 찾을 수 없음 scrapId :" + scrapId);
		return parseToDetailDto(dtos);
	}

	public ScrapDetailDto parseToDetailDto(List<ScrapDetailServiceDto> dtos) {
		ScrapDetailDto response = new ScrapDetailDto(dtos.get(0));
		List<ScrapImageDto> imageDtoList = response.getImageDtos();
		for (ScrapDetailServiceDto dto : dtos) {
			if (dto.getImageId() == null)
				break;
			ScrapImageDto imageDto = new ScrapImageDto(dto.getImageId(), dto.getImageKey());
			imageDtoList.add(imageDto);
		}
		return response;
	}
}
