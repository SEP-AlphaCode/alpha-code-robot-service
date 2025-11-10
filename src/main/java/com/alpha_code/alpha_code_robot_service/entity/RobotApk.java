package com.alpha_code.alpha_code_robot_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "robot_apk")
public class RobotApk {
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
    @Column(name = "file", nullable = false)
    private String file;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "description", columnDefinition = "text")
    private String description;


    @Column(name = "robot_model_id", nullable = false)
    private UUID robotModelId;

    @NotNull
    @Column(name = "is_require_license", nullable = false)
    private Boolean isRequireLicense;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "robot_model_id", insertable = false, updatable = false)
    private RobotModel robotModel;

    @OneToMany(mappedBy = "robotApk", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RobotApkAddon> robotApkAddons;
}
