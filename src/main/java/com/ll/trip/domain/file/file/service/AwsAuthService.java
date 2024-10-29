package com.ll.trip.domain.file.file.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.ll.trip.domain.file.file.dto.DeleteImageDto;
import com.ll.trip.domain.file.file.dto.PreSignedUrlResponseDto;
import com.ll.trip.domain.history.history.repository.HistoryRepository;
import com.ll.trip.domain.trip.scrap.repository.ScrapImageRepository;
import com.ll.trip.domain.trip.trip.dto.TripImageDeleteDto;
import com.ll.trip.domain.trip.trip.repository.TripRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AwsAuthService {
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;

	private final HistoryRepository historyRepository;
	private final ScrapImageRepository scrapImageRepository;
	private final TripRepository tripRepository;

	public PreSignedUrlResponseDto getPreSignedUrl(String prefix, int photoCnt) {
		List<String> preSignedUrls = new ArrayList<>();

		for (int i = 0; i < photoCnt; i++) {
			String fileName = createPath(prefix, null);
			GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucket, fileName);
			URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
			preSignedUrls.add(url.toString());
		}

		return new PreSignedUrlResponseDto(preSignedUrls);
	}

	public List<String> extractUrlFromPreSignedUrl(List<String> preSignedUrls) {
		List<String> abstractedUrls = new ArrayList<>();

		StringTokenizer st;

		for (String url : preSignedUrls) {
			st = new StringTokenizer(url, "?");
			addIfNotBlank(abstractedUrls, st.nextToken());
		}

		return abstractedUrls;
	}

	public List<String> findImageByTripId(List<String> urls, long tripId) {
		List<TripImageDeleteDto> dtos = tripRepository.findTripAndHistoryByTripId(tripId);

		if (dtos != null && !dtos.isEmpty()) {
			addIfNotBlank(urls, dtos.get(0).getTripThumbnail());
			for (TripImageDeleteDto dto : dtos) {
				addIfNotBlank(urls, dto.getHistoryThumbnail());
				addIfNotBlank(urls, dto.getHistoryImage());
			}
		}

		return urls;
	}

	private void addIfNotBlank(List<String> urls, String value) {
		if (value != null && !value.isBlank()) {
			urls.add(value);
		}
	}

	public List<String> extractKeyFromUrl(List<String> urls) {
		List<String> extractedKeys = new ArrayList<>();

		String postRemoveString = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/";

		for (String url : urls) {
			extractedKeys.add(url.replace(postRemoveString, ""));
		}

		return extractedKeys;
	}

	public void deleteUrls(List<String> urls) {
		deleteObjectByKey(extractKeyFromUrl(urls));
	}

	public void deleteImagesByScrapId(long scrapId) {
		List<String> urls = new ArrayList<>();
		addToListFromList(urls, getKeyFromScrapImagesByScrapId(scrapId));
		deleteUrls(urls);
	}

	public void deleteImagesByUserId(long userId) {
		List<String> urls = new ArrayList<>();
		addToListFromDeleteImageDto(urls, historyRepository.findHistoryImagesByUserId(userId));
		addToListFromList(urls, scrapImageRepository.findScrapImagesByUserId(userId));
		deleteUrls(urls);
	}

	private void addToListFromList(List<String> urls, List<String> imageList) {
		for (String url : imageList) {
			addIfNotBlank(urls, url);
		}
	}

	private void addToListFromDeleteImageDto(List<String> urls, List<DeleteImageDto> dtos) {
		for (DeleteImageDto dto : dtos) {
			addIfNotBlank(urls, dto.getImageUrl());
			addIfNotBlank(urls, dto.getThumbnail());
		}
	}

	private List<String> getKeyFromScrapImagesByScrapId(long scrapId) {
		return scrapImageRepository.findAllImageKeyByScrapId(scrapId);
	}

	@Transactional // 트랜잭션 처리
	public void deleteObjectByKey(List<String> keys) {
		for (String key : keys) {
			// 버킷에서 URL을 기반으로 오브젝트 삭제
			amazonS3.deleteObject(bucket, key);
		}
	}

	private GeneratePresignedUrlRequest getGeneratePreSignedUrlRequest(String bucket, String fileName) {
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
			new GeneratePresignedUrlRequest(bucket, fileName)
				.withMethod(HttpMethod.PUT)
				.withExpiration(getPreSignedUrlExpiration());
		generatePresignedUrlRequest.addRequestParameter(
			Headers.S3_CANNED_ACL,
			CannedAccessControlList.PublicRead.toString());
		return generatePresignedUrlRequest;
	}

	private Date getPreSignedUrlExpiration() {
		Date expiration = new Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * 5;
		expiration.setTime(expTimeMillis);
		return expiration;
	}

	private String createFileId() {
		return UUID.randomUUID().toString();
	}

	private String createPath(String prefix, String fileName) {
		String fileId = createFileId();
		if (fileName == null)
			return String.format("%s/%s", prefix, fileId);
		return String.format("%s/%s", prefix, fileId + fileName);
	}

	public void deleteImagesByTripId(long tripId) {
		List<String> urls = new ArrayList<>();
		addToListFromList(urls, findImageByTripId(new ArrayList<>(), tripId));
		addToListFromList(urls, scrapImageRepository.findAllImageKeyByTripId(tripId));
		deleteUrls(urls);
	}

	public void deleteImageByHistoryId(long historyId) {
		List<String> urls = new ArrayList<>();
		addToListFromDeleteImageDto(urls, List.of(historyRepository.findHistoryImages(historyId)));
		deleteUrls(urls);
	}
}
