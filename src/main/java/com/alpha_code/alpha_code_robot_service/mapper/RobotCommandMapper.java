package com.alpha_code.alpha_code_robot_service.mapper;

import com.alpha_code.alpha_code_robot_service.dto.RobotCommandDto;
import com.alpha_code.alpha_code_robot_service.entity.RobotCommand;

public class RobotCommandMapper {
    public static RobotCommandDto toDto (RobotCommand robotCommand) {
        if (robotCommand == null) return null;

        RobotCommandDto robotCommandDto = new RobotCommandDto();
        robotCommandDto.setId(robotCommand.getId());
        robotCommandDto.setCreatedDate(robotCommand.getCreatedDate());
        robotCommandDto.setLastUpdated(robotCommand.getLastUpdated());
        robotCommandDto.setStatus(robotCommand.getStatus());
        robotCommandDto.setRobotModelId(robotCommand.getRobotModelId());
        if (robotCommand.getRobotModel() != null) {
            robotCommandDto.setRobotModelName(robotCommand.getRobotModel().getName());
        }
        robotCommandDto.setCommandId(robotCommand.getCommandId());
        if (robotCommand.getCommand() != null) {
            robotCommandDto.setCommandName(robotCommand.getCommand().getName());
        }
        return robotCommandDto;
    }

    public static RobotCommand toEntity (RobotCommandDto robotCommandDto) {
        if (robotCommandDto == null) return null;

        RobotCommand robotCommand = new RobotCommand();
        robotCommand.setId(robotCommandDto.getId());
        robotCommand.setCreatedDate(robotCommandDto.getCreatedDate());
        robotCommand.setLastUpdated(robotCommandDto.getLastUpdated());
        robotCommand.setStatus(robotCommandDto.getStatus());
        robotCommand.setRobotModelId(robotCommandDto.getRobotModelId());
        robotCommand.setCommandId(robotCommandDto.getCommandId());
        return robotCommand;
    }
}
