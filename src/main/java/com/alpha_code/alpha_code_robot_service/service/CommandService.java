package com.alpha_code.alpha_code_robot_service.service;

import com.alpha_code.alpha_code_robot_service.dto.CommandDto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.entity.Command;

import java.util.UUID;

public interface CommandService {
    PagedResult<CommandDto> getAll(int page, int size, String name, Integer status);

    CommandDto getOne(UUID id);

    CommandDto getByName(String name);

    CommandDto create(CommandDto commandDto);

    CommandDto update(UUID id, CommandDto commandDto);

    CommandDto patch(UUID id, CommandDto commandDto);

    String delete(UUID id);

    CommandDto changeStatus(UUID id, Integer status);
}
