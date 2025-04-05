package com.ll.trip.domain.notification.firebase.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmMessageUtil {
	private final ExecutorService executor = Executors.newFixedThreadPool(10);

	public MulticastMessage buildMulticastMessage(List<String> tokenList, String title, String body,
		Map<String, String> data) {
		return MulticastMessage.builder()
			.addAllTokens(tokenList)
			.setNotification(Notification.builder()
				.setTitle(title)
				.setBody(body)
				.build())
			.putAllData(data)
			// Android 소리 설정
			.setAndroidConfig(AndroidConfig.builder()
				.setNotification(AndroidNotification.builder()
					.setSound("default")  // Android 기본 소리
					.setChannelId("trips")
					.build())
				.build())
			// iOS 소리 설정
			.setApnsConfig(ApnsConfig.builder()
				.setAps(Aps.builder()
					.setSound("default")  // iOS 기본 소리
					.build())
				.build())
			.build();
	}

	public void sendMessage(List<String> tokenList, String title, String body, Map<String, String> data) {
		if (tokenList.isEmpty())
			return;
		MulticastMessage message = buildMulticastMessage(tokenList, title, body, data);
		executor.submit(() -> FirebaseMessaging.getInstance().sendEachForMulticast(message));
	}
}
