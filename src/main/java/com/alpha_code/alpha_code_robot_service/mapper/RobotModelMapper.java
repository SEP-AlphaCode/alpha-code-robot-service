package com.alpha_code.alpha_code_robot_service.mapper;

import com.alpha_code.alpha_code_robot_service.dto.RobotModelDto;
import com.alpha_code.alpha_code_robot_service.entity.RobotModel;

public class RobotModelMapper {
    public static RobotModelDto toDto(RobotModel robotModel) {
        if (robotModel == null) return null;

        RobotModelDto robotModelDto = new RobotModelDto();
        robotModelDto.setId(robotModel.getId());
        robotModelDto.setName(robotModel.getName());
        robotModelDto.setFirmwareVersion(robotModel.getFirmwareVersion());
        robotModelDto.setCtrlVersion(robotModel.getCtrlVersion());
        robotModelDto.setCreatedDate(robotModel.getCreatedDate());
        robotModelDto.setLastUpdated(robotModel.getLastUpdated());
        robotModelDto.setStatus(robotModel.getStatus());
        return robotModelDto;
    }

    public static RobotModel toEntity(RobotModelDto dto) {
        if (dto == null) return null;

        RobotModel robotModel = new RobotModel();
        robotModel.setId(dto.getId());
        robotModel.setName(dto.getName());
        robotModel.setFirmwareVersion(dto.getFirmwareVersion());
        robotModel.setCtrlVersion(dto.getCtrlVersion());
        robotModel.setCreatedDate(dto.getCreatedDate());
        robotModel.setLastUpdated(dto.getLastUpdated());
        robotModel.setStatus(dto.getStatus());
        return robotModel;
    }
}
