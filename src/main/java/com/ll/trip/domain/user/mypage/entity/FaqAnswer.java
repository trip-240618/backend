package com.ll.trip.domain.user.mypage.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
public class FaqAnswer extends BaseEntity {
	@ManyToOne
	@JoinColumn(name = "faq_id")
	private Faq faq;

	@NotBlank
	private String title;

	@NotBlank
	private String content;
}
