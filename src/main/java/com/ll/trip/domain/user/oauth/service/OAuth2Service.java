package com.ll.trip.domain.user.oauth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ll.trip.domain.user.jwt.JwtTokenUtil;
import com.ll.trip.domain.user.user.dto.UserInfoDto;
import com.ll.trip.domain.user.user.entity.RefreshToken;
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

	public UserInfoDto registerUser(String oauthId, String nickName, String email, String profileImg, String provider, HttpServletResponse response) {
		String providerId = provider + oauthId;
		Optional<UserEntity> optUser = userRepository.findByProviderId(providerId);
		String uuid;
		String refreshToken;
		UserInfoDto userInfoDto;

		if (optUser.isPresent()) {
			UserEntity userEntity = optUser.get();
			RefreshToken foundRefreshToken = userService.findRefreshTokenByUserId(userEntity.getId());
			refreshToken = foundRefreshToken.getKeyValue();
			uuid = userEntity.getUuid();
			userInfoDto = new UserInfoDto(userEntity, "login");
		} else {
			if(oauthId == null || nickName == null || email == null) return null;

			uuid = userService.generateUUID();
			String tokenKey = jwtTokenUtil.createRefreshToken(uuid, List.of("USER"));

			RefreshToken createdRefreshToken = RefreshToken.builder()
				.keyValue(tokenKey)
				.build();

			UserEntity user = UserEntity
				.builder()
				.name(nickName)
				.roles("USER")
				.profileImg(profileImg)
				.providerId(providerId)
				.uuid(uuid)
				.email(email)
				.refreshToken(createdRefreshToken)
				.build();

			UserEntity userEntity = userRepository.save(user);
			userInfoDto = new UserInfoDto(userEntity, "register");
			refreshToken = createdRefreshToken.getKeyValue();
		}
		String newAccessToken = jwtTokenUtil.createAccessToken(uuid, List.of("USER"));

		userService.setTokenInCookie(newAccessToken, refreshToken, response);

		return userInfoDto;
	}
}
