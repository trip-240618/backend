package com.ll.trip.global.init;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.country.service.CountryService;
import com.ll.trip.domain.history.history.dto.HistoryCreateRequestDto;
import com.ll.trip.domain.history.history.dto.HistoryTagDto;
import com.ll.trip.domain.history.history.repository.HistoryRepository;
import com.ll.trip.domain.history.history.service.HistoryService;
import com.ll.trip.domain.trip.trip.dto.TripCreateDto;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.repository.TripMemberRepository;
import com.ll.trip.domain.trip.trip.repository.TripRepository;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.domain.user.oauth.service.OAuth2Service;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;
import com.ll.trip.domain.user.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Profile("local")
@Slf4j
@RequiredArgsConstructor
public class DevInit {
	private final UserService userService;
	private final OAuth2Service oAuth2Service;
	private final UserRepository userRepository;
	private final TripService tripService;
	private final TripRepository tripRepository;
	private final TripMemberRepository tripMemberRepository;
	private final HistoryRepository historyRepository;
	private final HistoryService historyService;
	private final CountryService countryService;

	@Bean
	@Transactional
	public ApplicationRunner initNotProd() {
		return args -> {
			// countryService.saveCountryImages(); 국기 이미지 저장할 때

			if (userRepository.count() == 0) {
				oAuth2Service.registerUser("test", "https://avatars.githubusercontent.com/u/109726278?v=4",
					"KAKAO3607862190", null, null);
			}

			UserEntity user = userService.findUserByUserId(1);

			if (tripMemberRepository.countByUser_Id(user.getId()) == 0) {
				String invitationCode = tripService.generateInvitationCode();
				Trip trip = tripService.createTrip(
					new TripCreateDto("testTrip1", 'j', LocalDate.of(2024, 9, 14), LocalDate.of(2024, 9, 20), "일본",
						"https://avatars.githubusercontent.com/u/109726278?v=4", "#83CF75"), invitationCode);

				tripService.joinTripById(trip, user, true);

				invitationCode = tripService.generateInvitationCode();
				trip = tripService.createTrip(
					new TripCreateDto("testTrip2", 'p', LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 10), "일본",
						"https://avatars.githubusercontent.com/u/109726278?v=4", "#83CF75"), invitationCode);

				tripService.joinTripById(trip, user, true);
			}

			Trip trip = tripRepository.findById(1L).get();

			if (historyRepository.countByTrip(trip) == 0) {
				historyService.createHistory(
					new HistoryCreateRequestDto(
						"https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f",
						// thumbnail
						"https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f",
						// imageUrl
						new BigDecimal("37.4220541"), // latitude
						new BigDecimal("-122.08532419999999"), // longitude
						LocalDate.of(2024, 9, 14), // photoDate
						"오사카에서 찍은 사진", // memo
						Arrays.asList(new HistoryTagDto("##FFEFF3","tag1"), new HistoryTagDto("##FFEFF3","tag2")) // tags 리스트
					),trip, user);

				historyService.createHistory(
					new HistoryCreateRequestDto(
						"https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f",
						// thumbnail
						"https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f",
						// imageUrl
						new BigDecimal("37.4220541"), // latitude
						new BigDecimal("-122.08532419999999"), // longitude
						LocalDate.of(2024, 9, 15), // photoDate
						"오사카에서 찍은 사진", // memo
						Arrays.asList(new HistoryTagDto("##FFEFF3","tag1"), new HistoryTagDto("##FFEFF3","tag2")) // tags 리스트
					),trip, user);
			}

		};
	}

}
