package com.alpha_code.alpha_code_robot_service.repository;

import com.alpha_code.alpha_code_robot_service.entity.Command;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommandRepository extends JpaRepository<Command, UUID> {
    Page<Command> getCommandsByStatusAndNameContaining(Integer status, String name, Pageable pageable);

    Page<Command> getCommandsByStatus(Integer status, Pageable pageable);

    Page<Command> getCommandsByNameContaining(String name, Pageable pageable);

    @Query("""
            SELECT e
            FROM Command e
            WHERE (:name IS NULL OR :name = '' OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:status IS NULL OR e.status = :status)
            AND (:status <> 0)
            """)
    Page<Command> searchAll(
            @Param("name") String name,
            @Param("status") Integer status,
            Pageable pageable
    );

    Optional<Command> getCommandByNameIgnoreCase(String name);

}