package com.alpha_code.alpha_code_robot_service.repository;

import com.alpha_code.alpha_code_robot_service.entity.RobotApkAddon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RobotApkAddonRepository extends JpaRepository<RobotApkAddon, UUID> {
}
