package com.ll.trip.domain.trip.history.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.global.base.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
public class History extends BaseEntity {
	@ManyToOne
	@JoinColumn(name = "trip_id")
	private Trip trip;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@NotBlank
	private String imageUrl;

	@Column(precision = 10, scale = 8)
	private BigDecimal latitude; //위도

	@Column(precision = 11, scale = 8)
	private BigDecimal longitude; //경도

	private LocalDateTime photoDate;

	private String memo;

	@OneToMany(cascade = CascadeType.ALL)
	private List<HistoryReply> historyReplies;
}
