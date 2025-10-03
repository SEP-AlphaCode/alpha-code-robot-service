package com.alpha_code.alpha_code_robot_service.repository;

import com.alpha_code.alpha_code_robot_service.entity.RobotCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RobotCommandRepository extends JpaRepository<RobotCommand, UUID> {
    @Query("""
            SELECT e
            FROM RobotCommand e
            WHERE (:robotModelId IS NULL OR e.robotModel.id = :robotModelId)
            AND (:commandId IS NULL OR e.command.id = :commandId)
            AND (:status IS NULL OR e.status = :status)
            AND (e.status <> 0)
            """)
    Page<RobotCommand> getAll(UUID robotModelId, UUID commandId, Integer status, Pageable pageable);
}
