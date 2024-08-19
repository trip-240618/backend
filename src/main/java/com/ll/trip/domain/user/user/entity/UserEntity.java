package com.ll.trip.domain.user.user.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.ll.trip.domain.alarm.alarm.entity.Notification;
import com.ll.trip.domain.plan.room.entity.Bookmark;
import com.ll.trip.domain.plan.room.entity.TripMember;
import com.ll.trip.domain.user.mypage.entity.UserProfile;
import com.ll.trip.global.base.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user", indexes = {
	@Index(name = "idx_uuid", columnList = "uuid")
})
public class UserEntity extends BaseEntity {

	@NotBlank
	private String name;
	@NotBlank
	private String providerId;
	@NotBlank
	private String uuid;
	@NotBlank
	private String profileImg;
	@NotBlank
	private String roles;
	private String email;
	private String fcmToken;

	@OneToMany(cascade = CascadeType.ALL)
	private List<RefreshToken> refreshTokens;

	@OneToMany(cascade = CascadeType.ALL)
	private List<UserProfile> userProfiles;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Notification> notifications;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Bookmark> bookmarks;

	@OneToMany(cascade = CascadeType.ALL)
	private List<TripMember> tripMembers;

	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();

		authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));

		if (List.of("admin").contains(roles)) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		return authorities;
	}
}
