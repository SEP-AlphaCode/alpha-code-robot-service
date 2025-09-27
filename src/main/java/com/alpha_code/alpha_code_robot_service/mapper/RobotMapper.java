package com.alpha_code.alpha_code_robot_service.mapper;

import com.alpha_code.alpha_code_robot_service.dto.RobotDto;
import com.alpha_code.alpha_code_robot_service.entity.Robot;

public class RobotMapper {
    public static RobotDto toDto(Robot robot) {
        if(robot==null){
            return null;
        }

        RobotDto robotDto = new RobotDto();
        robotDto.setId(robot.getId());
        robotDto.setSerialNumber(robot.getSerialNumber());
        robotDto.setCreatedDate(robot.getCreatedDate());
        robotDto.setLastUpdated(robot.getLastUpdated());
        robotDto.setStatus(robot.getStatus());
        robotDto.setAccountId(robot.getAccountId());
        robotDto.setRobotModelId(robot.getRobotModelId());
        if (robot.getRobotModel() != null) {
            robotDto.setRobotModelName(robot.getRobotModel().getName());
        }
        return robotDto;
    }

    public static Robot toEntity(RobotDto robotDto) {
        if(robotDto==null){
            return null;
        }

        Robot robot = new Robot();
        robot.setId(robotDto.getId());
        robot.setSerialNumber(robotDto.getSerialNumber());
        robot.setCreatedDate(robotDto.getCreatedDate());
        robot.setLastUpdated(robotDto.getLastUpdated());
        robot.setStatus(robotDto.getStatus());
        robot.setAccountId(robotDto.getAccountId());
        robot.setRobotModelId(robotDto.getRobotModelId());
        return robot;
    }
}
