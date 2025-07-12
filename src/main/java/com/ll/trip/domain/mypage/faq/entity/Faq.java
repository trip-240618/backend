package com.ll.trip.domain.mypage.faq.entity;

import com.ll.trip.global.base.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Faq extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "FAQ ID", example = "1")
	private Long id;

	@NotBlank
	@Setter
	@Schema(description = "FAQ ID", example = "1")
	private String type;

	@NotBlank
	@Setter
	private String title;

	@NotBlank
	@Setter
	private String content;
}
