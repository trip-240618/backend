package com.ll.trip.domain.user.oauth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.notification.notification.service.NotificationService;
import com.ll.trip.global.security.filter.jwt.JwtTokenUtil;
import com.ll.trip.domain.user.user.dto.UserInfoDto;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;
import com.ll.trip.domain.user.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
	private final UserRepository userRepository;
	private final UserService userService;
	private final JwtTokenUtil jwtTokenUtil;
	private final NotificationService notificationService;

	@Transactional
	public UserInfoDto whenLogin(String oauthId, String name, String email, String profileImg, String provider,
		String fcmToken, HttpServletResponse response) {
		String providerId = provider + oauthId;
		Optional<UserEntity> optUser = userRepository.findByProviderId(providerId);
		UserEntity user;
		String uuid;
		UserInfoDto userInfoDto;

		if (optUser.isEmpty()) {
			user = registerUser(name, profileImg, providerId, email, fcmToken);
			notificationService.createNotificationConfig(user);
			uuid = user.getUuid();
			userInfoDto = new UserInfoDto(user, "register");
		} else {
			user = optUser.get();
			uuid = user.getUuid();

			userService.updateFcmTokenByUserId(user.getId(), fcmToken);

			if (user.getNickname() == null)
				userInfoDto = new UserInfoDto(user, "register");
			else
				userInfoDto = new UserInfoDto(user, "login");
		}

		userService.createAndSetTokens(user.getId(), uuid, user.getNickname(), user.getAuthorities(), response);

		return userInfoDto;
	}

	@Transactional
	public UserEntity registerUser(String name, String profileImg, String providerId, String email,
		String fcmToken) {
		String uuid = userService.generateUUID();

		UserEntity user = UserEntity.builder()
			.name(name)
			.roles("USER")
			.profileImg(profileImg)
			.providerId(providerId)
			.uuid(uuid)
			.email(email)
			.fcmToken(fcmToken)
			.build();

		return userRepository.save(user);
	}
}
