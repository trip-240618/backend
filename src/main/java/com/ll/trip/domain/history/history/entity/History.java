package com.ll.trip.domain.history.history.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.global.base.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
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
public class History extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_id")
	private Trip trip;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@NotBlank
	private String thumbnail;

	@NotBlank
	private String imageUrl;

	@Column(precision = 10, scale = 8)
	private BigDecimal latitude; //위도

	@Column(precision = 11, scale = 8)
	private BigDecimal longitude; //경도

	private LocalDate photoDate;

	@Setter
	private String memo;

	private int likeCnt;

	private int replyCnt;

	@OneToMany(mappedBy = "history",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<HistoryReply> historyReplies = new ArrayList<>();

	@OneToMany(mappedBy = "history",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	@Setter
	private List<HistoryTag> historyTags = new ArrayList<>();

	@OneToMany(mappedBy = "history",cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	@Setter
	private List<HistoryLike> historyLikes = new ArrayList<>();
}
