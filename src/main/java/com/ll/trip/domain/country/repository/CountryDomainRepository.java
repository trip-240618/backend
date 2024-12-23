package com.ll.trip.domain.country.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ll.trip.domain.country.entity.CountryDomain;

public interface CountryDomainRepository extends JpaRepository<CountryDomain, Long> {

	@Query("""
		select cd.code
		from CountryDomain cd
		where cd.name = :name
		""")
	Optional<String> findByCountryName(String name);
}
