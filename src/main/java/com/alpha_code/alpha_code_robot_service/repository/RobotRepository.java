package com.alpha_code.alpha_code_robot_service.repository;

import com.alpha_code.alpha_code_robot_service.entity.Robot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RobotRepository extends JpaRepository<Robot, UUID> {
    @Query("""
        SELECT r 
        FROM Robot r
        WHERE (:serialNumber IS NULL OR :serialNumber = '' OR LOWER(r.serialNumber) LIKE LOWER(CONCAT('%', :serialNumber, '%')))
          AND (:accountId IS NULL OR r.accountId = :accountId)
          AND (:status IS NULL OR r.status = :status)
          AND (:status <> 0)
          AND (:robotModelId IS NULL OR r.robotModelId = :robotModelId)
    """)
    Page<Robot> searchRobots(
            @Param("serialNumber") String serialNumber,
            @Param("accountId") UUID accountId,
            @Param("status") Integer status,
            @Param("robotModelId") UUID robotModelId,
            Pageable pageable
    );

    Optional<Robot> findRobotBySerialNumberAndStatusNot(String serialNumber, Integer status);

}
