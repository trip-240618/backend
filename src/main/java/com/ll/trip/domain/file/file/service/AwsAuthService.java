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
import com.ll.trip.domain.file.file.dto.PreSignedUrlResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AwsAuthService {
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;

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

	public List<String> abstractUrlFromPresignedUrl(List<String> presignedUrls) {
		List<String> abstractedUrls = new ArrayList<>();

		StringTokenizer st;

		for (String url : presignedUrls) {
			st = new StringTokenizer(url, "?");
			abstractedUrls.add(st.nextToken());
		}

		return abstractedUrls;
	}

	public List<String> abstractKeyFromUrl(List<String> urls) {
		List<String> abstractedKeys = new ArrayList<>();

		String postRemoveString = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/";

		for (String url : urls) {
			abstractedKeys.add(url.replace(postRemoveString, ""));
		}

		return abstractedKeys;
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

}
