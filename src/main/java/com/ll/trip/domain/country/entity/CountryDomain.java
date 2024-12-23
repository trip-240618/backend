package com.ll.trip.domain.country.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Entity
@Getter
@Table(
	indexes = @Index(name = "idx_name", columnList = "name") // name 필드에 인덱스 생성
)
public class CountryDomain {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 2, nullable = false) // 데이터베이스 컬럼 길이를 2로 제한
	@Size(min = 2, max = 2, message = "Code must be exactly 2 characters long.") // 유효성 검사
	private String code;

	@Column(nullable = false)
	private String name;
}
