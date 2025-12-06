package com.alpha_code.alpha_code_robot_service.service;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.VideoCaptureDto;

import java.util.UUID;

public interface VideoCaptureService {

    PagedResult<VideoCaptureDto> getAll(int page, int size, UUID accountId, Integer status);

    VideoCaptureDto getById(UUID videoCaptureId);

    VideoCaptureDto createVideoCapture(VideoCaptureDto videoCaptureDto);

    VideoCaptureDto generateVideoCapture(UUID videoCaptureId, String description);

    void deleteVideoCapture(UUID videoCaptureId);
}
