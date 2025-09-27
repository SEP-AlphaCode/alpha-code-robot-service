package com.alpha_code.alpha_code_robot_service.service;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotCommandDto;
import com.alpha_code.alpha_code_robot_service.entity.RobotCommand;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface RobotCommandService {
    PagedResult<RobotCommandDto> getAll(int page, int size, UUID robotModelId, UUID commandId, Integer status);

    RobotCommandDto getOne(UUID id);

    PagedResult<RobotCommandDto> getAllByRobotModelNameAndCommandName(int page, int size, String robotModelName, String commandName, Integer status);

    RobotCommandDto create(RobotCommandDto robotCommandDto);

    RobotCommandDto update(UUID id, RobotCommandDto robotCommandDto);

    RobotCommandDto patch(UUID id, RobotCommandDto robotCommandDto);

    String delete(UUID id);

    RobotCommandDto changeStatus(UUID id, Integer status);
}
