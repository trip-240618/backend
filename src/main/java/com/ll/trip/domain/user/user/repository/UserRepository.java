package com.ll.trip.domain.user.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ll.trip.domain.user.user.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUuid(String uuid);

	Optional<UserEntity> findByProviderId(String providerId);

	@Modifying
	@Query("""
		update UserEntity u
		set u.fcmToken = :fcmToken
		where u.id = :userId
		""")
	int updateFcmTokenByUserId(Long userId, String fcmToken);
}
