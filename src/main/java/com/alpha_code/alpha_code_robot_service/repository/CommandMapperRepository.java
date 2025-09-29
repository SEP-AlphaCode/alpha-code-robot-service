package com.alpha_code.alpha_code_robot_service.repository;

import com.alpha_code.alpha_code_robot_service.dto.CommandMapperDto;
import com.alpha_code.alpha_code_robot_service.entity.CommandMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommandMapperRepository extends JpaRepository<CommandMapper, UUID> {
    @Query("""
        SELECT cm
        FROM CommandMapper cm
        WHERE (:activityId IS NULL OR cm.activityId = :activityId)
          AND (:status IS NULL OR cm.status = :status)
          AND (:commandId IS NULL OR cm.commandId = :commandId)
    """)
    Page<CommandMapper> searchCommandMappers(
            @Param("activityId") UUID activityId,
            @Param("status") Integer status,
            @Param("commandId") UUID commandId,
            Pageable pageable
    );

    List<CommandMapper> getCommandMappersByCommandIdAndStatusNot(UUID commandId, Integer status);


}
