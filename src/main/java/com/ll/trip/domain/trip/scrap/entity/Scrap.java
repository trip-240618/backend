package com.ll.trip.domain.trip.scrap.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.global.base.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Scrap extends BaseEntity {
	@ManyToOne
	@JoinColumn(name = "trip_id")
	private Trip trip;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@NotBlank
	private String title;

	@NotBlank
	private String content;

	private String color;

}
