package com.ll.trip.domain.user.mypage.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.global.base.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
public class UserProfile extends BaseEntity {
	@MapsId
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@NotBlank
	private Boolean activePlanNotification;

	@NotBlank
	private Boolean activeReplyNotification;

	@NotBlank
	private Boolean activeAdNotification;
}
