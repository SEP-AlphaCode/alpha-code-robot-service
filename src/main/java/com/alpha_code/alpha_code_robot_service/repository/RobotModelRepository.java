package com.alpha_code.alpha_code_robot_service.repository;

import com.alpha_code.alpha_code_robot_service.entity.RobotModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RobotModelRepository extends JpaRepository<RobotModel, UUID> {
    @Query("""
        SELECT rm
        FROM RobotModel rm
        WHERE (:name IS NULL OR :name = '' OR LOWER(rm.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:firmwareVersion IS NULL OR :firmwareVersion = '' OR LOWER(rm.firmwareVersion) LIKE LOWER(CONCAT('%', :firmwareVersion, '%')))
          AND (:ctrlVersion IS NULL OR :ctrlVersion = '' OR LOWER(rm.ctrlVersion) LIKE LOWER(CONCAT('%', :ctrlVersion, '%')))
          AND (:status IS NULL OR rm.status = :status)
          AND rm.status <> 0
    """)
    Page<RobotModel> searchRobotModels(
            @Param("name") String name,
            @Param("firmwareVersion") String firmwareVersion,
            @Param("ctrlVersion") String ctrlVersion,
            @Param("status") Integer status,
            Pageable pageable
    );

    Optional<RobotModel> getRobotModelByNameIgnoreCaseAndStatusNot(String name, Integer status);
}
