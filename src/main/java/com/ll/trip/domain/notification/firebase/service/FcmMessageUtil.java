package com.ll.trip.domain.notification.firebase.service;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.ll.trip.domain.notification.firebase.dto.MessageDto;
import com.ll.trip.global.handler.exception.ServerApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FcmMessageUtil {

	public MulticastMessage buildMulticastMessage(MessageDto messageDto) {
		return MulticastMessage.builder()
			.addAllTokens(messageDto.getTokenList())
			.setNotification(Notification.builder()
				.setTitle(messageDto.getTitle())
				.setBody(messageDto.getBody())
				.build())
			.putAllData(messageDto.getData())
			.build();
	}

	public BatchResponse sendMessage(MessageDto messageDto){
		MulticastMessage message = buildMulticastMessage(messageDto);
		try {
			return FirebaseMessaging.getInstance().sendEachForMulticast(message);
		} catch (FirebaseMessagingException e) {
			throw new ServerApiException(e);
		}
	}
}
