package com.alpha_code.alpha_code_robot_service.dto;

import com.alpha_code.alpha_code_robot_service.enums.RobotCommandEnum;
import com.alpha_code.alpha_code_robot_service.enums.RobotEnum;
import com.alpha_code.alpha_code_robot_service.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RobotCommandDto {
    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    private Integer status;

    @NotNull(message = "robotModelId is required", groups = {OnCreate.class})
    private UUID robotModelId;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private String robotModelName;

    @NotNull(message = "commandId is required", groups = {OnCreate.class})
    private UUID commandId;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private String commandName;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return RobotCommandEnum.fromCode(this.status);
    }
}
