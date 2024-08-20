package com.ll.trip.domain.trip.plan.entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.flight.entity.Flight;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.global.base.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class PlanP extends BaseEntity {
	@ManyToOne
	@JoinColumn(name = "trip_id")
	private Trip trip;

	private LocalDate startDate;

	private long order;

	private boolean locker;

	private String writerUuid;

	@NotBlank
	private String title;

	private boolean checkbox;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Flight> flights;
}
