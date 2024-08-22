package com.ll.trip.domain.flight.entity;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.trip.plan.entity.PlanJ;
import com.ll.trip.domain.trip.plan.entity.PlanP;
import com.ll.trip.global.base.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Flight extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "plan_j_id")
	private PlanJ planJ;

	@ManyToOne
	@JoinColumn(name = "plan_p_id")
	private PlanP planP;

	private String flightCode;

	private String flightNum;

	private String departureIata;

	private String departureAirport;

	private LocalDateTime departureDate;

	private String arrivalIata;

	private String arrivalAirport;

	private LocalDateTime arrivalDate;

}
