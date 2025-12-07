package com.ll.trip.domain.util.version.controller;

import com.ll.trip.domain.util.version.entity.Version;
import com.ll.trip.domain.util.version.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Version", description = "앱 버전 API")
public class VersionController {
    private final VersionService versionService;

    @GetMapping("/version/last")
    @Operation(summary = "최신 버전 정보")
    @ApiResponse(responseCode = "200", description = "최신 버전 정보 반환", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Version.class))})
    public ResponseEntity<?> getLastVersion() {
        Version version = versionService.getLastVersion();
        return ResponseEntity.ok(version);
    }

    @PostMapping("/version/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "버전 정보 생성")
    @ApiResponse(responseCode = "200", description = "버전 정보 생성 (관리자만 가능)", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Version.class))})
    public ResponseEntity<?> createVersion(@RequestBody Version version) {
        Version res = versionService.createVersion(version);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/version/modify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "버전 정보 수정")
    @ApiResponse(responseCode = "200", description = "버전 정보 수정( 관리자만 가능, 최신 버전만 보여주기 때문에 그냥 새로 생성해도 됨)", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Version.class))})
    public ResponseEntity<?> modifyVersion(@RequestBody Version version) {
        Version res = versionService.updateVersion(version);
        return ResponseEntity.ok(res);
    }

}
