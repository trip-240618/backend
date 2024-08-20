package com.ll.trip.domain.trip.trip.entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.trip.history.entity.History;
import com.ll.trip.domain.trip.history.entity.HistoryTag;
import com.ll.trip.domain.trip.plan.entity.PlanJ;
import com.ll.trip.domain.trip.plan.entity.PlanP;
import com.ll.trip.domain.trip.scrap.entity.Scrap;
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

	@OneToMany(cascade = CascadeType.ALL)
	private List<PlanJ> planJs;

	@OneToMany(cascade = CascadeType.ALL)
	private List<PlanP> planPs;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Scrap> scraps;

	@OneToMany(cascade = CascadeType.ALL)
	private List<History> histories;

	@OneToMany(cascade = CascadeType.ALL)
	private List<HistoryTag> historyTags;
}
