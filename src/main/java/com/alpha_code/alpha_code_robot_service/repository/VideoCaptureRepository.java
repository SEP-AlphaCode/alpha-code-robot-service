package com.alpha_code.alpha_code_robot_service.repository;

import com.alpha_code.alpha_code_robot_service.entity.VideoCapture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoCaptureRepository extends JpaRepository<VideoCapture, UUID> {

    @Query("""
        SELECT vc
        FROM VideoCapture vc
        WHERE (:accountId IS NULL OR vc.accountId = :accountId)
          AND (COALESCE(:status, vc.status) = vc.status)
          AND vc.status <> 0
    """)
    Page<VideoCapture> searchVideoCaptures(
            @Param("accountId") UUID accountId,
            @Param("status") Integer status,
            Pageable pageable
    );
}
