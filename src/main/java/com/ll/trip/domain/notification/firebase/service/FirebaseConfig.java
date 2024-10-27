package com.ll.trip.domain.notification.firebase.service;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

	@Value("${firebase.secret-path}")
	private String secretPath;

	@PostConstruct
	public void initialize() throws IOException {
		GoogleCredentials credentials = GoogleCredentials
			.fromStream(new ClassPathResource(secretPath).getInputStream())
			.createScoped(Arrays.asList(
				"https://www.googleapis.com/auth/firebase",
				"https://www.googleapis.com/auth/cloud-platform",
				"https://www.googleapis.com/auth/firebase.readonly"));
		credentials.refreshAccessToken();

		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(credentials)
			.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}
	}
}
