package com.ll.trip.domain.country.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.country.entity.Country;
import com.ll.trip.domain.country.repository.CountryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {
	private final CountryRepository countryRepository;

	@Value(value = "${file.country.path}")
	private String filePath;
	@Transactional
	public void saveCountryImages() throws IOException {
		File imageFolder = new File(filePath);
		File[] imageFiles = imageFolder.listFiles();

		for (File imageFile : imageFiles) {
			String fileName = imageFile.getName().replace(".gif", "");
			Optional<Country> countryOptional = countryRepository.findByCountryCode(fileName);

			if (countryOptional.isPresent()) {
				byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
				log.info(fileName + " " + imageBytes.length + " bytes");
				// Country 객체에 이미지 설정
				countryRepository.updateFlagImageByCountryCode(fileName, imageBytes);
			}
		}
	}
}
