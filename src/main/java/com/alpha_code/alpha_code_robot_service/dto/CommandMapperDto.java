package com.alpha_code.alpha_code_robot_service.dto;

import com.alpha_code.alpha_code_robot_service.enums.CommandEnum;
import com.alpha_code.alpha_code_robot_service.enums.CommandMapperEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandMapperDto implements Serializable {
    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private UUID id;

    private UUID activityId;

    private UUID actionId;

    private UUID expressionId;

    private UUID danceId;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    private Integer status;

    private UUID commandId;

    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private String commandName;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return CommandMapperEnum.fromCode(this.status);
    }
}
