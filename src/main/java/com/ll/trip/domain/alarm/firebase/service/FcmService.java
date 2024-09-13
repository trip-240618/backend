package com.ll.trip.domain.alarm.firebase.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.ll.trip.domain.alarm.firebase.dto.AlarmResponseDto;
import com.ll.trip.domain.alarm.firebase.dto.FcmMessageDto;

@Service
public class FcmService {
	@Value("${firebase.secret-path}")
	private String secret_path;

	public int sendMessageTo(AlarmResponseDto responseDto) throws IOException {

		String message = makeMessage(responseDto);
		RestTemplate restTemplate = new RestTemplate();

		restTemplate.getMessageConverters()
			.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + getAccessToken());

		HttpEntity entity = new HttpEntity<>(message, headers);

		String API_URL = "<https://fcm.googleapis.com/v1/projects/tripstory-14935/messages:send>";
		ResponseEntity response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

		System.out.println(response.getStatusCode());

		return response.getStatusCode() == HttpStatus.OK ? 1 : 0;
	}

	private String getAccessToken() throws IOException {
		String firebaseConfigPath = secret_path;

		GoogleCredentials googleCredentials = GoogleCredentials
			.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
			.createScoped(List.of("<https://www.googleapis.com/auth/cloud-platform>"));

		googleCredentials.refreshIfExpired();
		return googleCredentials.getAccessToken().getTokenValue();
	}

	private String makeMessage(AlarmResponseDto responseDto) throws JsonProcessingException {

		ObjectMapper om = new ObjectMapper();
		FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
			.message(FcmMessageDto.Message.builder()
				.token(responseDto.getToken())
				.notification(FcmMessageDto.Notification.builder()
					.title(responseDto.getTitle())
					.body(responseDto.getBody())
					.image(null)
					.build()
				).build()).validateOnly(false).build();

		return om.writeValueAsString(fcmMessageDto);
	}
}
