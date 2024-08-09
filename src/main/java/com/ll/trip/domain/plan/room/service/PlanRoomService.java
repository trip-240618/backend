package com.ll.trip.domain.plan.room.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.file.file.dto.UploadImageRequestBody;
import com.ll.trip.domain.file.file.entity.TripImage;
import com.ll.trip.domain.plan.room.entity.Trip;
import com.ll.trip.domain.plan.room.repository.PlanRoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanRoomService {
	private final PlanRoomRepository planRoomRepository;

	public Long createRoom() {
		Trip trip = Trip.builder().name("testRoom").build();
		trip = planRoomRepository.save(trip);
		return trip.getId();
	}

	@Transactional
	public void uploadImgUrlByRoomId(Long roomId, UploadImageRequestBody request) throws NullPointerException{
		log.info("roomId : " + roomId);
		Optional<Trip> optRoom = planRoomRepository.findById(roomId);

		Trip room = optRoom.get();
		for(String url : request.getImgUrls()) {
			TripImage img = TripImage.builder()
				.uri(url)
				.build();

			room.addImg(img);
		}

		planRoomRepository.save(room);
	}
}
