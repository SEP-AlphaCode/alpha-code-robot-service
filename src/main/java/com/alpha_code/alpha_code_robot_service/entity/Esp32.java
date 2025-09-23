package com.alpha_code.alpha_code_robot_service.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "esp_32")
public class Esp32 {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Size(max = 255)
    @NotNull
    @Column(name = "mac_address", nullable = false)
    private String macAddress;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "firmware_version")
    private Integer firmwareVersion;

    @Column(name = "metadata", columnDefinition = "jsonb")
    @Type(JsonType.class)
    private JsonNode metadata;

    @Size(max = 255)
    @Column(name = "topic_pub")
    private String topicPub;

    @Size(max = 255)
    @NotNull
    @Column(name = "topic_sub", nullable = false)
    private String topicSub;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Integer createdAt;

    @Column(name = "last_updated")
    private Integer lastUpdated;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;
}