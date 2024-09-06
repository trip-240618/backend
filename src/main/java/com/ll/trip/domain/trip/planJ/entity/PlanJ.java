package com.ll.trip.domain.trip.planJ.entity;

import java.math.BigDecimal;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.global.base.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@Table(name = "plan_j")
public class PlanJ extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "trip_id")
	private Trip trip;

	@Setter
	private Integer dayAfterStart;

	@Setter
	private LocalTime startTime;

	@Setter
	private Integer orderByDate;

	private boolean locker;

	private String writerUuid;

	@Setter
	@Column(precision = 10, scale = 8)
	private BigDecimal latitude; //위도

	@Setter
	@Column(precision = 11, scale = 8)
	private BigDecimal longitude; //경도

	@Setter
	@NotBlank
	private String title;

	@Setter
	private String memo;

	private Integer flightId;
}
