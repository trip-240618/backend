package com.ll.trip.domain.user.oauth.service;

import com.ll.trip.domain.notification.notification.service.NotificationService;
import com.ll.trip.domain.oauth2.OAuthAttributes;
import com.ll.trip.domain.user.user.dto.UserInfoDto;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;
import com.ll.trip.domain.user.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public UserInfoDto whenLogin(String oauthId, String name, String email, String profileImg, String provider,
                                 String fcmToken, HttpServletResponse response) {
        UserEntity user = whenLogin(oauthId, name, email, profileImg, provider, fcmToken);

        userService.createAndSetTokens(user.getId(), user.getUuid(), user.getNickname(), user.getAuthorities(), response);

        return new UserInfoDto(user);
    }

    @Transactional
    public UserEntity whenLogin(String oauthId, String name, String email, String profileImg, String provider,
                                 String fcmToken) {
        String providerId = provider + oauthId;
        Optional<UserEntity> optUser = userRepository.findByProviderId(providerId);
        UserEntity user;

        if (optUser.isEmpty()) {
            user = registerUser(name, profileImg, providerId, email, fcmToken);
            notificationService.createNotificationConfig(user);
        } else {
            user = optUser.get();
            userService.updateFcmTokenByUserId(user.getId(), fcmToken);

            user = userRepository.findById(user.getId()).get(); // 나중에 fcm토큰 업데이트를 분리하면 없애도 됨
        }

        return user;
    }

    @Transactional
    public UserEntity whenLogin(OAuthAttributes attributes, String provider) {
        return this.whenLogin(attributes.getOauthId()
                , attributes.getName()
                , attributes.getEmail()
                , attributes.getPicture()
                , provider
                , null
                );
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
