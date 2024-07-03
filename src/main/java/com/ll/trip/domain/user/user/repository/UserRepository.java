package com.ll.trip.domain.user.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.user.user.dto.UserInfoDto;
import com.ll.trip.domain.user.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	UserInfoDto findByUuid(String uuid);
}
