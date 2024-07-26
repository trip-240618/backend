package com.ll.trip.domain.file.file.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.ll.trip.domain.file.file.dto.PreSignedUrlRequestBody;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AwsAuthService {
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;

	public PreSignedUrlResponseDto getPreSignedUrl(PreSignedUrlRequestBody requestBody) {
		List<String> preSignedUrls = new ArrayList<>();

		for (int i = 0; i < requestBody.getPhotoCount(); i++) {
			String fileName = createPath(requestBody.getPrefix(), null);
			GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucket, fileName);
			URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
			preSignedUrls.add(url.toString());
		}

		return new PreSignedUrlResponseDto(preSignedUrls);
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
