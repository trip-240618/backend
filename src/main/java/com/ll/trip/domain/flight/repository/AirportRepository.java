package com.ll.trip.domain.flight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ll.trip.domain.flight.entity.Airport;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {


	@Query("SELECT a FROM Airport a WHERE a.iata = :iata1 OR a.iata = :iata2")
	List<Airport> findByIata(String iata1, String iata2);
}
