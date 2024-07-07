package com.ll.trip.domain.user.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.user.user.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
