package com.ll.trip.healthCheck.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TestEntity {
	@Id
	private Long id;

	private String value;
}
