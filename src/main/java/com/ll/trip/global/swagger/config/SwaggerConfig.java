package com.ll.trip.global.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI getOpenApi() {

		return new OpenAPI()
			.components(new Components())
			.info(getInfo());

	}

	private Info getInfo() {
		return new Info()
			.version("1.0.0")
			.description("COMMERCE REST API DOC")
			.title("COMMERCE");
	}


}
