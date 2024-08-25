package com.ll.trip.domain.user.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ll.trip.domain.user.user.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUuid(String uuid);

	Optional<UserEntity> findByProviderId(String providerId);
}
