package com.ll.trip.domain.user.oauth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.user.jwt.JwtTokenUtil;
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

	@Transactional
	public UserInfoDto registerUser(String oauthId, String name, String email, String profileImg, String provider,
		HttpServletResponse response) {
		String providerId = provider + oauthId;
		Optional<UserEntity> optUser = userRepository.findByProviderId(providerId);
		String uuid;
		String refreshToken;
		UserInfoDto userInfoDto;

		if (optUser.isEmpty()) {
			uuid = userService.generateUUID();
			refreshToken = jwtTokenUtil.createRefreshToken(uuid, List.of("USER"));

			UserEntity user = UserEntity
				.builder()
				.name(name)
				.roles("USER")
				.profileImg(profileImg)
				.providerId(providerId)
				.uuid(uuid)
				.email(email)
				.build();

			user = userRepository.save(user);

			userInfoDto = new UserInfoDto(user, "register");
		} else {
			UserEntity user = optUser.get();
			uuid = user.getUuid();

			refreshToken = jwtTokenUtil.createRefreshToken(uuid, List.of("USER"));

			if(user.getNickname() == null) userInfoDto = new UserInfoDto(user, "register");
			else userInfoDto = new UserInfoDto(user, "login");
		}
		String newAccessToken = jwtTokenUtil.createAccessToken(uuid, List.of("USER"));

		userService.setTokenInCookie(newAccessToken, refreshToken, response);

		return userInfoDto;
	}
}
