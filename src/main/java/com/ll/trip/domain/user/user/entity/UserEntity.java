package com.ll.trip.domain.user.user.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.ll.trip.domain.alarm.alarm.entity.Notification;
import com.ll.trip.domain.trip.history.entity.History;
import com.ll.trip.domain.trip.history.entity.HistoryReply;
import com.ll.trip.domain.trip.scrap.entity.Scrap;
import com.ll.trip.domain.trip.trip.entity.Bookmark;
import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.domain.user.mypage.entity.NotificationConfig;
import com.ll.trip.global.base.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user", indexes = {
	@Index(name = "idx_uuid", columnList = "uuid")
})
public class UserEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String providerId;

	@NotBlank
	private String uuid;

	@Setter
	private String nickname;

	@Setter
	private String profileImg;

	@NotBlank
	private String roles;

	private String email;

	private String fcmToken;

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<NotificationConfig> notificationConfigs = new ArrayList<>();

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Notification> notifications = new ArrayList<>();

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Bookmark> bookmarks = new ArrayList<>();

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<TripMember> tripMembers = new ArrayList<>();

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Scrap> scraps = new ArrayList<>();

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<History> histories = new ArrayList<>();

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<HistoryReply> historyReplies = new ArrayList<>();

	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();

		authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));

		if (Objects.equals("admin", roles)) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		return authorities;
	}
}
