package com.ll.trip.global.security.userDetail;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class SecurityUser extends User implements OAuth2User {
    private String uuid;

    private String name;// 이름

    private String profileImage;

    public SecurityUser(String uuid, String name, String providerId, String password, String profileImage, Collection<? extends GrantedAuthority> authorities) {
        super(providerId, password, authorities);
        this.uuid = uuid;
        this.name = name;
        this.profileImage = profileImage;
    }

    public SecurityUser(String uuid, String name, String providerId, String password, String profileImage, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(providerId, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
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
