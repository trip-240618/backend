package com.ll.trip.domain.plan.room.entity;

import java.util.ArrayList;
import java.util.List;

import com.ll.trip.domain.file.file.entity.TripImage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
public class Trip {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	private String name;

	@OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TripImage> TripImages = new ArrayList<>();

	public void addImg(TripImage img) {
		TripImages.add(img);
		img.setTrip(this);
	}
}
