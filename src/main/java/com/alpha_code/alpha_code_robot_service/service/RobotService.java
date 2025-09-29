package com.alpha_code.alpha_code_robot_service.service;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotDto;

import java.util.List;
import java.util.UUID;

public interface RobotService {
    PagedResult<RobotDto> getAll(int page, int size, String serialNumber, UUID accountId, Integer status, UUID robotModelId);

    RobotDto getById(UUID id);

    List<RobotDto> getAllByAccountId(UUID accountId);

    RobotDto create(RobotDto robotDto);

    RobotDto update(UUID id, RobotDto robotDto);

    RobotDto patch(UUID id, RobotDto robotDto);

    String delete(UUID id);

    RobotDto changeStatus(UUID id, Integer status);
}
