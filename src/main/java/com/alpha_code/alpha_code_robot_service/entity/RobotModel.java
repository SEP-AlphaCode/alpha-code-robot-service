package com.alpha_code.alpha_code_robot_service.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "robot_model")
public class RobotModel {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "firmware_version", nullable = false)
    private String firmwareVersion;

    @Size(max = 255)
    @NotNull
    @Column(name = "ctrl_version", nullable = false)
    private String ctrlVersion;

    @Column(name = "robot_prompt", nullable = false, columnDefinition = "text")
    private String robotPrompt;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;

    //Relationship
    @OneToMany(mappedBy = "robotModel", fetch = FetchType.LAZY)
    private List<Robot> robots;


    @OneToMany(mappedBy = "robotModel", fetch = FetchType.LAZY)
    private List<RobotApk> robotApks;

}