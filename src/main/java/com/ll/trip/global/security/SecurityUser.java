package com.ll.feelko.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class SecurityUser extends User implements OAuth2User {

    private long id;

    private String name;// 이름

    private String profileImage;

    private String status;

    public SecurityUser(long id, String name, String username, String password, String profileImage, Collection<? extends GrantedAuthority> authorities, String status) {
        super(username, password, authorities);
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
        this.status = status;
    }

    public SecurityUser(long id, String name, String username, String password, String profileImage, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String status) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
        this.status = status;
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
