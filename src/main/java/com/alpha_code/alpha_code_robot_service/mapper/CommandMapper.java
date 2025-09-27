package com.alpha_code.alpha_code_robot_service.mapper;

import com.alpha_code.alpha_code_robot_service.dto.CommandDto;
import com.alpha_code.alpha_code_robot_service.entity.Command;

public class CommandMapper {
    public static CommandDto toDto(Command command) {
        if (command == null) {
            return null;
        }

        CommandDto commandDto = new CommandDto();
        commandDto.setId(command.getId());
        commandDto.setName(command.getName());
        commandDto.setDescription(command.getDescription());
        commandDto.setCreatedDate(command.getCreatedDate());
        commandDto.setLastUpdated(command.getLastUpdated());
        commandDto.setStatus(command.getStatus());
        return  commandDto;
    }

    public static Command toEntity(CommandDto commandDto) {
        if (commandDto == null) {
            return null;
        }

        Command command = new Command();
        command.setId(commandDto.getId());
        command.setName(commandDto.getName());
        command.setDescription(commandDto.getDescription());
        command.setCreatedDate(commandDto.getCreatedDate());
        command.setLastUpdated(commandDto.getLastUpdated());
        command.setStatus(commandDto.getStatus());
        return command;
    }
}
