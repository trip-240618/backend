package com.ll.trip.domain.file.file.service;

import com.ll.trip.domain.file.file.dto.DeleteImageDto;
import com.ll.trip.domain.file.file.dto.PreSignedUrlResponseDto;
import com.ll.trip.domain.history.history.repository.HistoryRepository;
import com.ll.trip.domain.trip.scrap.repository.ScrapImageRepository;
import com.ll.trip.domain.trip.trip.dto.TripImageDeleteDto;
import com.ll.trip.domain.trip.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AwsAuthService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final HistoryRepository historyRepository;
    private final ScrapImageRepository scrapImageRepository;
    private final TripRepository tripRepository;

    public String createPresignedPutUrl(String fileName) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toExternalForm();
    }

    public PreSignedUrlResponseDto getPreSignedUrl(String prefix, int photoCnt) {
        List<String> preSignedUrls = new ArrayList<>();

        for (int i = 0; i < photoCnt; i++) {
            String fileName = createPath(prefix, null);
            preSignedUrls.add(createPresignedPutUrl(fileName));
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
        // 삭제할 객체들의 키 목록을 ObjectIdentifier로 변환
        List<ObjectIdentifier> toDelete = keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .collect(Collectors.toList());

        // Delete 객체 생성
        Delete delete = Delete.builder()
                .objects(toDelete)
                .build();

        // 삭제 요청 생성
        DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(delete)
                .build();

        // 요청 실행
        DeleteObjectsResponse response = s3Client.deleteObjects(deleteRequest);
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
