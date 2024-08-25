package com.ll.trip.domain.trip.trip.entity;

import java.time.LocalDate;
import java.util.ArrayList;
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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Trip extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Setter
	private String name;

	@NotBlank
	private String invitationCode;

	private char type;

	@NotNull
	@Setter
	private LocalDate startDate;

	@NotNull
	@Setter
	private LocalDate endDate;

	@NotBlank
	private String country;

	@NotBlank
	@Setter
	private String thumbnail;

	@NotBlank
	@Setter
	private String labelColor;

	@OneToMany(mappedBy = "trip",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Bookmark> bookmarks = new ArrayList<>();

	@OneToMany(mappedBy = "trip",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<TripMember> tripMembers = new ArrayList<>();

	@OneToMany(mappedBy = "trip",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PlanJ> planJs  = new ArrayList<>();

	@OneToMany(mappedBy = "trip",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PlanP> planPs = new ArrayList<>();

	@OneToMany(mappedBy = "trip",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Scrap> scraps = new ArrayList<>();

	@OneToMany(mappedBy = "trip",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<History> histories = new ArrayList<>();

	@OneToMany(mappedBy = "trip",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<HistoryTag> historyTags = new ArrayList<>();
}
