package com.ll.trip.domain.history.history.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryListDto {
    @Schema(
            description = "history의 pk",
            example = "12")
    private long id;

    @Schema(
            description = "프로필 축소버전",
            example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
    private String profileImage;

    @NotBlank
    @Schema(
            description = "축소된 사진",
            example = "https://trip-story.s3.ap-northeast-2.amazonaws.com/photoTest/c3396416-1e2e-4d0d-9a82-788831e5ac1f")
    private String thumbnail;

    @Schema(description = "위도",
            example = "37.4220541")
    private BigDecimal latitude; //위도

    @Schema(description = "경도",
            example = "-122.08532419999999")
    private BigDecimal longitude; //경도

    @Schema(description = "좋아요 여부",
            example = "false")
    private boolean like;

    @Schema(description = "좋아요 수",
            example = "1")
    private int likeCnt;

    @Schema(description = "댓글 수",
            example = "3")
    private int replyCnt;

    @Schema(
            description = "사진 날짜",
            example = "2024-08-22")
    private LocalDate photoDate;

    public HistoryListDto(HistoryServiceDto serviceDto) {
        this.id = serviceDto.getId();
        this.profileImage = serviceDto.getProfileImage();
        this.thumbnail = serviceDto.getThumbnail();
        this.latitude = serviceDto.getLatitude();
        this.longitude = serviceDto.getLongitude();
        this.like = serviceDto.isLike();
        this.likeCnt = serviceDto.getLikeCnt();
        this.replyCnt = serviceDto.getReplyCnt();
        this.photoDate = serviceDto.getPhotoDate();
    }
}