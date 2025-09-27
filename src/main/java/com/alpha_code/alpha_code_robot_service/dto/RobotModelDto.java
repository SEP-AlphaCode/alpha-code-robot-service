package com.alpha_code.alpha_code_robot_service.dto;


import com.alpha_code.alpha_code_robot_service.enums.RobotEnum;
import com.alpha_code.alpha_code_robot_service.enums.RobotModelEnum;
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
public class RobotModelDto implements Serializable {
    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotNull(message = "Name is required", groups = {OnCreate.class})
    private String name;

    @NotNull(message = "Firmware version is required", groups = {OnCreate.class})
    private String firmwareVersion;

    @NotNull(message = "Ctrl version is required", groups = {OnCreate.class})
    private String ctrlVersion;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    private Integer status;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return RobotModelEnum.fromCode(this.status);
    }
}
