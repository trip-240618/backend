package com.ll.trip.global.security.userDetail;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class SecurityUser extends User implements OAuth2User {
    private final Long id;

    private final String uuid;

    private final String name;// 이름

    private final String profileImage;

    public SecurityUser(Long id, String uuid, String name, String providerId, String profileImage, Collection<? extends GrantedAuthority> authorities) {
        super(uuid, providerId, authorities); //username = uuid, password = providerId
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.profileImage = profileImage;
    }

    public SecurityUser(Long id, String uuid, String name, String providerId, String profileImage, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(uuid, providerId, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.profileImage = profileImage;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
