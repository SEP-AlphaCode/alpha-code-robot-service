package com.alpha_code.alpha_code_robot_service.dto;

import com.alpha_code.alpha_code_robot_service.enums.Esp32Enum;
import com.alpha_code.alpha_code_robot_service.enums.RobotEnum;
import com.alpha_code.alpha_code_robot_service.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
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
public class RobotDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotNull(message = "Serial number is required", groups = {OnCreate.class})
    private String serialNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    private Integer status;

    @NotNull(message = "Account id is required", groups = {OnCreate.class})
    private UUID accountId;

    @NotNull(message = "Robot model id is required", groups = {OnCreate.class})
    private UUID robotModelId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String robotModelName;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return RobotEnum.fromCode(this.status);
    }
}
