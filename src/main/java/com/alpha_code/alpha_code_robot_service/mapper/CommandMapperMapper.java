package com.alpha_code.alpha_code_robot_service.mapper;

import com.alpha_code.alpha_code_robot_service.dto.CommandMapperDto;
import com.alpha_code.alpha_code_robot_service.entity.CommandMapper;

public class CommandMapperMapper {
    public static CommandMapperDto toDto(CommandMapper commandMapper) {
        if (commandMapper == null) {
            return null;
        }

        CommandMapperDto commandMapperDto = new CommandMapperDto();
        commandMapperDto.setId(commandMapper.getId());
        commandMapperDto.setActivityId(commandMapper.getActivityId());
        commandMapperDto.setActionId(commandMapper.getActionId());
        commandMapperDto.setExpressionId(commandMapper.getExpressionId());
        commandMapperDto.setDanceId(commandMapper.getDanceId());
        commandMapperDto.setCreatedDate(commandMapper.getCreatedDate());
        commandMapperDto.setLastUpdated(commandMapper.getLastUpdated());
        commandMapperDto.setStatus(commandMapper.getStatus());
        commandMapperDto.setCommandId(commandMapper.getCommandId());
        if (commandMapper.getCommand() != null) {
            commandMapperDto.setCommandName(commandMapper.getCommand().getName());
        }

        return commandMapperDto;
    }

    public static CommandMapper toEntity(CommandMapperDto commandMapperDto) {
        if (commandMapperDto == null) {
            return null;
        }

        CommandMapper commandMapper = new CommandMapper();
        commandMapper.setId(commandMapperDto.getId());
        commandMapper.setActivityId(commandMapperDto.getActivityId());
        commandMapper.setActionId(commandMapperDto.getActionId());
        commandMapper.setExpressionId(commandMapperDto.getExpressionId());
        commandMapper.setDanceId(commandMapperDto.getDanceId());
        commandMapper.setCreatedDate(commandMapperDto.getCreatedDate());
        commandMapper.setLastUpdated(commandMapperDto.getLastUpdated());
        commandMapper.setStatus(commandMapperDto.getStatus());
        commandMapper.setCommandId(commandMapperDto.getCommandId());
        return  commandMapper;
    }
}
