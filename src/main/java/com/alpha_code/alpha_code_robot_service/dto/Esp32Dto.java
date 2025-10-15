package com.alpha_code.alpha_code_robot_service.dto;

import com.alpha_code.alpha_code_robot_service.enums.Esp32Enum;
import com.alpha_code.alpha_code_robot_service.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Esp32Dto implements Serializable {
    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotNull(message = "accountId is required", groups = {OnCreate.class})
    private UUID accountId;

    @NotNull(message = "macAddress is required", groups = {OnCreate.class})
    @Size(min = 12, max = 12, message = "macAddress must be 12 characters")
    private String macAddress;

    @NotNull(message = "name is required", groups = {OnCreate.class})
    private String name;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastSeen;

    @NotNull(message = "firmwareVersion is required", groups = {OnCreate.class})
    private Integer firmwareVersion;

    private JsonNode metadata;

    @NotNull(message = "topicPub is required", groups = {OnCreate.class})
    private String topicPub;

    @NotNull(message = "topicSub is required", groups = {OnCreate.class})
    private String topicSub;

    private String message;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    private Integer status;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return Esp32Enum.fromCode(this.status);
    }
}
