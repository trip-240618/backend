package com.ll.trip.domain.flight.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.trip.trip.entity.Trip;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Flight {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "trip_id")
	private Trip trip;

	private String airlineCode;

	private int airlineNumber;

	private String departureDate;

	private String departureAirport;

	private String departureAirport_kr;

	private String arrivalDate;

	private String arrivalAirport;

	private String arrivalAirport_kr;
}
