package com.ll.trip.domain.version.entity;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Version {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "androidVersion", nullable = true)
	private String androidVersion;

	@Column(name = "iosVersion", nullable = true)
	private String iosVersion;

	@Column(name = "force", nullable = true)
	private String force;

	@CreatedDate
	private String createDate;
}


