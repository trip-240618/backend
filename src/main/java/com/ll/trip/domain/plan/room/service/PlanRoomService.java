package com.ll.trip.domain.plan.room.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.file.file.dto.UploadImageRequestBody;
import com.ll.trip.domain.file.file.entity.RoomImage;
import com.ll.trip.domain.plan.room.entity.PlanRoom;
import com.ll.trip.domain.plan.room.repository.PlanRoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanRoomService {
	private final PlanRoomRepository planRoomRepository;

	public Long createRoom() {
		PlanRoom planRoom = PlanRoom.builder().name("testRoom").build();
		planRoom = planRoomRepository.save(planRoom);
		return planRoom.getId();
	}

	@Transactional
	public void uploadImgUrlByRoomId(Long roomId, UploadImageRequestBody request) throws NullPointerException{
		log.info("roomId : " + roomId);
		Optional<PlanRoom> optRoom = planRoomRepository.findById(roomId);

		PlanRoom room = optRoom.get();
		for(String url : request.getImgUrls()) {
			RoomImage img = RoomImage.builder()
				.uri(url)
				.build();

			room.addImg(img);
		}

		planRoomRepository.save(room);
	}
}
