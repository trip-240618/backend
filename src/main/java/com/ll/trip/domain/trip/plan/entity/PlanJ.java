package com.ll.trip.domain.trip.plan.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.flight.entity.Flight;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.global.base.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "plan_j")
public class PlanJ extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "trip_id")
	private Trip trip;

	private int dayAfterStart;

	private LocalTime startTime;

	private long orderByDate;

	private boolean locker;

	private String writerUuid;

	@NotBlank
	private String title;

	private String memo;

	@OneToMany(mappedBy = "planJ",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Flight> flights  = new ArrayList<>();
}
