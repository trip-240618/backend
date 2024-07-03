package com.ll.trip.global.security.userDetail;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class SecurityUser extends User implements OAuth2User {

    private long id;

    private String name;// 이름

    private String profileImage;

    public SecurityUser(Long id, String name, String providerId, String password, String profileImage, Collection<? extends GrantedAuthority> authorities) {
        super(providerId, password, authorities);
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
    }

    public SecurityUser(long id, String name, String providerId, String password, String profileImage, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(providerId, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
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
