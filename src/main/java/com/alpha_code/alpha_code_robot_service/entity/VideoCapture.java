package com.alpha_code.alpha_code_robot_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "video_capture")
public class VideoCapture {
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

    @NotNull
    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "video_url")
    private String videoUrl;

    @NotNull
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @NotNull
    @Column(name = "is_created", nullable = false)
    private Boolean isCreated;

    @NotNull
    @Column(name = "description")
    private String description;
}
