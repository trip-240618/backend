package com.ll.trip.domain.user.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.user.user.entity.DeletedUser;

public interface DeletedUserRepository extends JpaRepository<DeletedUser, Long> {
}
