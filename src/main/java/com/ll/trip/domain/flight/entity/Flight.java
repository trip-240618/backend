package com.ll.trip.domain.flight.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.trip.plan.entity.PlanJ;
import com.ll.trip.domain.trip.plan.entity.PlanP;
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
public class Flight extends BaseEntity {
	@ManyToOne
	@JoinColumn(name = "plan_j_id")
	private PlanJ planJ;

	@ManyToOne
	@JoinColumn(name = "plan_p_id")
	private PlanP planp;

	private LocalDate startDate;

	private LocalTime startTime;

	private long order;

	private boolean locker;

	private String writerUuid;

	@NotBlank
	private String title;

	private String memo;
}
