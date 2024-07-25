package com.ll.trip.domain.plan.plan.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Plan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	private Long roomId;

	@CreatedDate
	private LocalDateTime createDate;

	@LastModifiedDate
	private LocalDateTime modifyDate;

	//index는 mySql에서 사용해서 사용불가
	private Long idx;

	private String title;
	private String content;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlanImage> imgUris = new ArrayList<>();

	public void addImg(PlanImage img) {
		imgUris.add(img);
		img.setPlan(this);
	}
}
