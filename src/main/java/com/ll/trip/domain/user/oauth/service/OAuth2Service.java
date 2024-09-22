package com.ll.trip.domain.user.oauth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.user.jwt.JwtTokenUtil;
import com.ll.trip.domain.user.mypage.service.MypageService;
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
	private final MypageService mypageService;

	@Transactional
	public UserInfoDto whenLogin(String oauthId, String name, String email, String profileImg, String provider,
		String fcmToken, HttpServletResponse response) {
		String providerId = provider + oauthId;
		Optional<UserEntity> optUser = userRepository.findByProviderId(providerId);
		String refreshToken;
		String uuid;
		UserInfoDto userInfoDto;

		if (optUser.isEmpty()) {
			userInfoDto = registerUser(name, profileImg, providerId, email, fcmToken);
			uuid = userInfoDto.getUuid();
			refreshToken = jwtTokenUtil.createRefreshToken(uuid, List.of("USER"));
		} else {
			UserEntity user = optUser.get();
			uuid = user.getUuid();

			userService.updateFcmTokenByUserId(user.getId(), fcmToken);
			mypageService.createNotificationConfig(user);
			refreshToken = jwtTokenUtil.createRefreshToken(uuid, List.of("USER"));

			if (user.getNickname() == null)
				userInfoDto = new UserInfoDto(user, "register");
			else
				userInfoDto = new UserInfoDto(user, "login");
		}
		String newAccessToken = jwtTokenUtil.createAccessToken(uuid, List.of("USER"));

		userService.setTokenInCookie(newAccessToken, refreshToken, response);

		return userInfoDto;
	}

	public UserInfoDto registerUser(String name, String profileImg, String providerId, String email,
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

		user = userRepository.save(user);

		return new UserInfoDto(user, "register");
	}
}
