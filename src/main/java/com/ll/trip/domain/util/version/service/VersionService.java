package com.ll.trip.domain.util.version.service;

import com.ll.trip.domain.util.version.entity.Version;
import com.ll.trip.domain.util.version.repository.VersionRepository;
import com.ll.trip.global.handler.exception.NoSuchDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VersionService {
    private final VersionRepository versionRepository;

    public Version getLastVersion() {
        Optional<Version> optVersion = versionRepository.findTopByOrderByCreateDateDesc();
        if (optVersion.isEmpty()) throw new NoSuchDataException("버전 정보를 찾을 수 없습니다.");
        return optVersion.get();
    }

    @Transactional
    public Version createVersion(Version version) {
        return versionRepository.save(version);
    }

    @Transactional
    public Version updateVersion(Version version) {
        return versionRepository.save(version);
    }

}
