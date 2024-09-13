package com.ll.trip.domain.trip.trip.service;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.ll.trip.domain.trip.trip.repository.TripRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvitationCodeGenerator {
	private final TripRepository tripRepository;

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int CODE_LENGTH = 8;
	private static Random random = new Random();

	public String generateUniqueCode() {
		String code;
		do {
			code = generateRandomCode();
		} while (isCodeExist(code));

		return code;
	}

	private String generateRandomCode() {
		StringBuilder code = new StringBuilder(CODE_LENGTH);
		for (int i = 0; i < CODE_LENGTH; i++) {
			int index = random.nextInt(CHARACTERS.length());
			code.append(CHARACTERS.charAt(index));
		}
		return code.toString();
	}

	private boolean isCodeExist(String code) {
		return tripRepository.existsByInvitationCode(code);
	}

}
