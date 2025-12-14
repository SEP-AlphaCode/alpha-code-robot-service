package com.alpha_code.alpha_code_robot_service.repository;

import com.alpha_code.alpha_code_robot_service.entity.Esp32;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface Esp32Repository extends JpaRepository<Esp32, UUID> {
    @Query("""
        SELECT e 
        FROM Esp32 e
        WHERE (:accountId IS NULL OR e.accountId = :accountId)
          AND (:name IS NULL OR :name = '' OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:firmwareVersion IS NULL OR e.firmwareVersion = :firmwareVersion)
          AND (:status IS NULL OR e.status = :status)
          AND (e.status <> 0)
    """)
    Page<Esp32> searchAll(
            @Param("accountId") UUID accountId,
            @Param("name") String name,
            @Param("firmwareVersion") Integer firmwareVersion,
            @Param("status") Integer status,
            Pageable pageable
    );

    Optional<Esp32> findByAccountId(UUID accountId);

    // Return an ESP32 for an account with the given status (e.g. status == 1 for active)
    Optional<Esp32> findByAccountIdAndStatus(UUID accountId, Integer status);
}
