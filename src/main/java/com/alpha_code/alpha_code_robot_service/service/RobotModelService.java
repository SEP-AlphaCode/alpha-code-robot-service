package com.alpha_code.alpha_code_robot_service.service;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotModelDto;
import com.alpha_code.alpha_code_robot_service.entity.RobotModel;

import java.util.List;
import java.util.UUID;

public interface RobotModelService {
    PagedResult<RobotModelDto> getAll(int page, int size, String name, String firmwareVersion, String ctrlVersion, Integer status);
    RobotModelDto getById(UUID id);
    RobotModelDto create(RobotModelDto robotModelDto);
    RobotModelDto update(UUID id, RobotModelDto robotModelDto);
    RobotModelDto patch(UUID id, RobotModelDto robotModelDto);
    String delete(UUID id);
    RobotModelDto changeStatus(UUID id, Integer status);

    List<RobotModel> findAllByIds(List<UUID> uuidList);
}
