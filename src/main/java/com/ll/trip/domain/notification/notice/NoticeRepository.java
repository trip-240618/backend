package com.ll.trip.domain.notification.notice;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.trip.domain.notification.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
