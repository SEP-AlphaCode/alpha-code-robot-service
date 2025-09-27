package com.alpha_code.alpha_code_robot_service.repository;

import com.alpha_code.alpha_code_robot_service.entity.Esp32;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface Esp32Repository extends JpaRepository<Esp32, UUID> {
    @Query("""
        SELECT e 
        FROM Esp32 e
        WHERE (:accountId IS NULL OR e.accountId = :accountId)
          AND (:macAddress IS NULL OR :macAddress = '' OR LOWER(e.macAddress) LIKE LOWER(CONCAT('%', :macAddress, '%')))
          AND (:name IS NULL OR :name = '' OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:firmwareVersion IS NULL OR e.firmwareVersion = :firmwareVersion)
          AND (:topicPub IS NULL OR :topicPub = '' OR LOWER(e.topicPub) LIKE LOWER(CONCAT('%', :topicPub, '%')))
          AND (:topicSub IS NULL OR :topicSub = '' OR LOWER(e.topicSub) LIKE LOWER(CONCAT('%', :topicSub, '%')))
          AND (:status IS NULL OR e.status = :status)
          AND (:status <> 0)
    """)
    Page<Esp32> searchAll(
            @Param("accountId") UUID accountId,
            @Param("macAddress") String macAddress,
            @Param("name") String name,
            @Param("firmwareVersion") Integer firmwareVersion,
            @Param("topicPub") String topicPub,
            @Param("topicSub") String topicSub,
            @Param("status") Integer status,
            Pageable pageable
    );
}
