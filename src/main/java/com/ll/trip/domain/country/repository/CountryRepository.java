package com.ll.trip.domain.country.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.country.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {

	Optional<Country> findByCountryCode(String fileName);

	@Modifying
	@Query("UPDATE Country c SET c.flagImage = :file WHERE c.countryCode = :code")
	void updateFlagImageByCountryCode(String code, byte[] file);

	Optional<Country> findByCountryName(String countryName);
}
