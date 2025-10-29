package com.alpha_code.alpha_code_robot_service.dto;

import com.alpha_code.alpha_code_robot_service.entity.RobotApk;
import com.alpha_code.alpha_code_robot_service.enums.RobotApkEnum;
import com.alpha_code.alpha_code_robot_service.enums.RobotEnum;
import com.alpha_code.alpha_code_robot_service.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RobotApkDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotNull(message = "Phiên bản là bắt buộc", groups = {OnCreate.class})
    @Pattern(regexp = "\\d+\\.\\d+(\\.\\d+)?", message = "Phiên bản phải có dạng x.y hoặc x.y.z")
    private String version;

    @NotNull(message = "Miêu tả là bắt buộc", groups = {OnCreate.class})
    private String description;

    @NotNull(message = "Id của loại robot là bắt buộc", groups = {OnCreate.class})
    private UUID robotModelId;

    @NotNull(message = "Cần giấy phép sử dụng là bắt buộc", groups = {OnCreate.class})
    private Boolean isRequireLicense;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String robotModelName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private MultipartFile file;

    private Integer status;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return RobotApkEnum.fromCode(this.status);
    }
}
