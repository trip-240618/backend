package com.ll.trip.domain.notification.firebase.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

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
			.build();
	}

	public void sendMessage(List<String> tokenList, String title, String body, Map<String, String> data) {
		if (tokenList.isEmpty())
			return;
		MulticastMessage message = buildMulticastMessage(tokenList, title, body, data);
		executor.submit(() -> FirebaseMessaging.getInstance().sendEachForMulticast(message));
	}
}
