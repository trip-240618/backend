package com.ll.trip.domain.plan.room.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.user.user.entity.UserEntity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
public class Bookmark {
	@EmbeddedId
	private BookmarkId id;

	@ManyToOne
	@MapsId("userId") // BookmarkId에 정의된 필드명과 일치해야 함
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne
	@MapsId("tripId") // BookmarkId에 정의된 필드명과 일치해야 함
	@JoinColumn(name = "trip_id")
	private Trip trip;
}
