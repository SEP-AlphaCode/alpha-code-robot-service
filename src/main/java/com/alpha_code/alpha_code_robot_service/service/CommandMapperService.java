package com.alpha_code.alpha_code_robot_service.service;

import com.alpha_code.alpha_code_robot_service.dto.CommandMapperDto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommandMapperService {
    PagedResult<CommandMapperDto> searchCommandMappers(int page, int size, UUID activityId, Integer status, UUID commandId);

    CommandMapperDto getById (UUID id);

    List<CommandMapperDto> getByCommandName(String commandName);

    CommandMapperDto create (CommandMapperDto commandMapperDto);

    CommandMapperDto update (UUID id, CommandMapperDto commandMapperDto);

    CommandMapperDto patch (UUID id, CommandMapperDto commandMapperDto);

    String delete (UUID id);

    CommandMapperDto changeStatus (UUID id, Integer status);

}
