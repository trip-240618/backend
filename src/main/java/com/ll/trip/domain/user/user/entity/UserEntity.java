package com.ll.trip.domain.user.user.entity;

import com.ll.trip.domain.history.history.entity.History;
import com.ll.trip.domain.history.history.entity.HistoryLike;
import com.ll.trip.domain.history.history.entity.HistoryReply;
import com.ll.trip.domain.notification.notification.entity.Notification;
import com.ll.trip.domain.notification.notification.entity.NotificationConfig;
import com.ll.trip.domain.trip.trip.entity.Bookmark;
import com.ll.trip.domain.trip.trip.entity.TripMember;
import com.ll.trip.global.base.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	private String name;

	@NotBlank
	private String providerId;

	@Setter
	private String memo;

	@NotBlank
	private String uuid;

	@Setter
	private String nickname;

	@Setter
	private String thumbnail;

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
	private List<History> histories = new ArrayList<>();

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<HistoryReply> historyReplies = new ArrayList<>();

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<HistoryLike> historyLikes = new ArrayList<>();

	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();

		authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));

		if (this.roles.equals("admin")) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		return authorities;
	}
}
