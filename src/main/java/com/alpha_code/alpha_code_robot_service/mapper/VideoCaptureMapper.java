package com.alpha_code.alpha_code_robot_service.mapper;

import com.alpha_code.alpha_code_robot_service.dto.VideoCaptureDto;
import com.alpha_code.alpha_code_robot_service.entity.VideoCapture;

public class VideoCaptureMapper {

    public static VideoCaptureDto toDto(VideoCapture videoCapture) {
        if (videoCapture == null) return null;

        VideoCaptureDto dto = new VideoCaptureDto();
        dto.setId(videoCapture.getId());
        dto.setImage(videoCapture.getImage());
        dto.setVideoUrl(videoCapture.getVideoUrl());
        dto.setAccountId(videoCapture.getAccountId());
        dto.setIsCreated(videoCapture.getIsCreated());
        dto.setDescription(videoCapture.getDescription());
        dto.setCreatedDate(videoCapture.getCreatedDate());
        dto.setLastUpdated(videoCapture.getLastUpdated());
        dto.setStatus(videoCapture.getStatus());
        return dto;
    }

    public static VideoCapture toEntity(VideoCaptureDto dto) {
        if (dto == null) return null;

        VideoCapture videoCapture = new VideoCapture();
        videoCapture.setId(dto.getId());
        videoCapture.setImage(dto.getImage());
        videoCapture.setVideoUrl(dto.getVideoUrl());
        videoCapture.setAccountId(dto.getAccountId());
        videoCapture.setIsCreated(dto.getIsCreated());
        videoCapture.setDescription(dto.getDescription());
        videoCapture.setCreatedDate(dto.getCreatedDate());
        videoCapture.setLastUpdated(dto.getLastUpdated());
        videoCapture.setStatus(dto.getStatus());
        return videoCapture;
    }
}

