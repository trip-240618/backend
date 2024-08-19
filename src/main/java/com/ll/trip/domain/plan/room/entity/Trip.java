package com.ll.trip.domain.plan.room.entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.global.base.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.OneToMany;
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
public class Trip extends BaseEntity {
	@NotBlank
	private String name;

	@NotBlank
	private String invitationCode;

	private char type;

	@NotBlank
	private LocalDate startDate;

	@NotBlank
	private LocalDate endDate;

	@NotBlank
	private String country;

	@NotBlank
	private String thumbnail;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Bookmark> bookmarks;

	@OneToMany(cascade = CascadeType.ALL)
	private List<TripMember> tripMembers;
}
