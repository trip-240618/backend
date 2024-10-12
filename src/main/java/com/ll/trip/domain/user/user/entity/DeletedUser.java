package com.ll.trip.domain.user.user.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
public class DeletedUser {
	@Id
	private Long id;

	@NotBlank
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
}
