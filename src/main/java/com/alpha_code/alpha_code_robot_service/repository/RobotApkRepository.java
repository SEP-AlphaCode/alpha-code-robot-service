package com.alpha_code.alpha_code_robot_service.repository;

import com.alpha_code.alpha_code_robot_service.entity.RobotApk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RobotApkRepository extends JpaRepository<RobotApk, UUID> {
    @Query("""
    SELECT ra
    FROM RobotApk ra
    JOIN FETCH ra.robotModel rm
    WHERE (:search IS NULL OR :search = '' 
        OR LOWER(ra.version) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(ra.description) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(rm.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND ra.status <> 0
    ORDER BY ra.isRequireLicense ASC, ra.lastUpdated DESC
""")
    Page<RobotApk> getAlls(@Param("search") String search, Pageable pageable);

    Optional<RobotApk> findRobotApkById(UUID id);
}
