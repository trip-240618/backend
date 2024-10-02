package com.ll.trip.domain.country.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Country {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String countryCode;

	@Column(precision = 10, scale = 8)
	private BigDecimal latitude; //위도

	@Column(precision = 11, scale = 8)
	private BigDecimal longitude; //경도

	private String countryName;

	@Column(columnDefinition = "MEDIUMBLOB")
	private byte[] flagImage;
}
