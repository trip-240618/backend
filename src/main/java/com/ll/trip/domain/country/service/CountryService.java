package com.ll.trip.domain.country.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.country.entity.Country;
import com.ll.trip.domain.country.repository.CountryDomainRepository;
import com.ll.trip.domain.country.repository.CountryRepository;
import com.ll.trip.global.handler.exception.NoSuchDataException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CountryService {
	private final CountryRepository countryRepository;
	private final CountryDomainRepository countryDomainRepository;

	// @Value(value = "${file.country.path}") //국기 이미지 저장할 때만 사용
	// private String filePath;
	//
	// @Transactional
	// public void saveCountryImages() throws IOException {
	// 	File imageFolder = new File(filePath);
	// 	File[] imageFiles = imageFolder.listFiles();
	//
	// 	for (File imageFile : imageFiles) {
	// 		String fileName = imageFile.getName().replace(".gif", "");
	// 		Optional<Country> countryOptional = countryRepository.findByCountryCode(fileName);
	//
	// 		if (countryOptional.isPresent()) {
	// 			byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
	// 			log.info(fileName + " " + imageBytes.length + " bytes");
	// 			// Country 객체에 이미지 설정
	// 			countryRepository.updateFlagImageByCountryCode(fileName, imageBytes);
	// 		}
	// 	}
	// }

	public Country findCountryByName(String countryName) {
		return countryRepository.findByCountryName(countryName)
			.orElseThrow(() -> new NoSuchDataException("Country not found with countryName: " + countryName));
	}

	public List<String> findAllCountryNameLike(String keyword) {
		return countryRepository.findCountryNameLike(keyword);
	}

	public String findCountryDomain(String name) {
		Optional<String> optDomain = countryDomainRepository.findByCountryName(name);
		return optDomain.orElse("kr");
	}
}
