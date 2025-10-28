package com.alpha_code.alpha_code_robot_service.service;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotApkDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface RobotApkService {
    PagedResult<RobotApkDto> getAlls(int page, int size, String search);
    String getFilePathByApkId(UUID apkId, UUID accountId);
    RobotApkDto create(RobotApkDto dto, MultipartFile file);
    RobotApkDto updateApkFile(UUID apkId, MultipartFile file);
    RobotApkDto update(UUID id, RobotApkDto robotApkDto);
    void delete(UUID id);
}
