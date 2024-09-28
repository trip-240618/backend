package com.ll.trip.domain.alarm.firebase.service;

import java.io.IOException;

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
		GoogleCredentials credentials = GoogleCredentials.fromStream(new ClassPathResource(secretPath).getInputStream());

		FirebaseOptions options = new FirebaseOptions.Builder()
			.setCredentials(credentials)
			.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}
	}
}
