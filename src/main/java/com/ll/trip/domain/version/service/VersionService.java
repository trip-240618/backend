package com.ll.trip.domain.version.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.version.entity.Version;
import com.ll.trip.domain.version.repository.VersionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VersionService {
	private final VersionRepository versionRepository;
	public Version getLastVersion(){
		return versionRepository.findTopByOrderByCreateDateDesc().get();
	}

}
