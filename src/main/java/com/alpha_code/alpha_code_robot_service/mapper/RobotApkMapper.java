package com.alpha_code.alpha_code_robot_service.mapper;

import com.alpha_code.alpha_code_robot_service.dto.RobotApkDto;
import com.alpha_code.alpha_code_robot_service.entity.RobotApk;

public class RobotApkMapper {
    public static RobotApkDto toDto(RobotApk robotApk) {
        if (robotApk == null) {
            return null;
        }
        RobotApkDto dto = new RobotApkDto();
        dto.setId(robotApk.getId());
        dto.setVersion(robotApk.getVersion());
        dto.setDescription(robotApk.getDescription());
        dto.setLastUpdated(robotApk.getLastUpdated());
        dto.setCreatedDate(robotApk.getCreatedDate());
        dto.setStatus(robotApk.getStatus());
        dto.setRobotModelId(robotApk.getRobotModelId());
        if(robotApk.getRobotModel() != null){
            dto.setRobotModelName(robotApk.getRobotModel().getName());
        }
        dto.setIsRequireLicense(robotApk.getIsRequireLicense());
        return dto;
    }

    public static RobotApk toEntity(RobotApkDto dto) {
        if (dto == null) {
            return null;
        }
        RobotApk robotApk = new RobotApk();
        robotApk.setId(dto.getId());
        robotApk.setVersion(dto.getVersion());
        robotApk.setDescription(dto.getDescription());
        robotApk.setLastUpdated(dto.getLastUpdated());
        robotApk.setCreatedDate(dto.getCreatedDate());
        robotApk.setStatus(dto.getStatus());
        robotApk.setRobotModelId(dto.getRobotModelId());
        robotApk.setIsRequireLicense(dto.getIsRequireLicense());

        return robotApk;
    }
}
