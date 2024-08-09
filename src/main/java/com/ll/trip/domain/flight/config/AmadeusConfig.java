package com.ll.trip.domain.flight.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amadeus.Amadeus;

@Configuration
public class AmadeusConfig {
	@Value("${data.amadeus.api-key}")
	private String apiKey;

	@Value("${data.amadeus.secret-key}")
	private String secretKey;


	@Bean
	public Amadeus amadeus(){
		return Amadeus.builder(apiKey,secretKey).build();
	}
}
