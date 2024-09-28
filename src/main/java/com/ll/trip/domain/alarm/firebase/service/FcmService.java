package com.ll.trip.domain.alarm.firebase.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FcmService {

	public int sendMulticastMessage(List<String> fcmTokens, String title, String body) throws IOException,
		FirebaseMessagingException {
		// Android 알림 설정
		AndroidConfig androidConfig = AndroidConfig.builder()
			.setNotification(AndroidNotification.builder()
				.setTitle(title)
				.setBody(body)
				.setColor("#f45342")
				.build())
			.setPriority(AndroidConfig.Priority.HIGH)
			.build();

		// iOS 알림 설정
		ApnsConfig apnsConfig = ApnsConfig.builder()
			.setAps(Aps.builder()
				.setAlert(ApsAlert.builder()
					.setTitle(title)
					.setBody(body)
					.build())
				.setSound("default")
				.build())
			.build();

		// Web 알림 설정 (WebPush 알림)
		WebpushConfig webpushConfig = WebpushConfig.builder()
			.setNotification(WebpushNotification.builder()
				.setTitle(title)
				.setBody(body)
				.build())
			.build();

		// MulticastMessage를 사용하여 여러 기기로 메시지 전송
		MulticastMessage message = MulticastMessage.builder()
			.addAllTokens(fcmTokens)
			.setNotification(Notification.builder().setTitle(title).setBody(body).setImage(null).build())
			.setAndroidConfig(androidConfig)
			.setApnsConfig(apnsConfig)
			.setWebpushConfig(webpushConfig)
			.build();

		// FirebaseMessaging을 통해 메시지 전송
		BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

		// 응답 출력 (성공한 전송 수와 실패한 전송 수)
		log.info("failure count: " + response.getFailureCount());

		return response.getFailureCount();
	}
}
