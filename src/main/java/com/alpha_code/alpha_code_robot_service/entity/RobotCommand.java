package com.alpha_code.alpha_code_robot_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "robot_command")
public class RobotCommand {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "robot_model_id", nullable = false)
    private UUID robotModelId;

    @Column(name = "command_id", nullable = false)
    private UUID commandId;

    //Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "robot_model_id", insertable = false, updatable = false)
    private RobotModel robotModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "command_id", insertable = false, updatable = false)
    private Command command;

}