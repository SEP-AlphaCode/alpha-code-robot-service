package com.alpha_code.alpha_code_robot_service.controller;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotApkDto;
import com.alpha_code.alpha_code_robot_service.service.RobotApkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/robot-apks")
@RequiredArgsConstructor
@Tag(name = "Robot Apks")
@Validated
public class RobotApkController {
    private final RobotApkService robotApkService;

    @GetMapping()
    @Operation(summary = "Get all robot apks")
    public PagedResult<RobotApkDto> getAlls(@RequestParam(value = "page", defaultValue = "1") int page,@RequestParam(value = "size", defaultValue = "10") int size,@RequestParam(value = "search", defaultValue = "") String search) {
        return robotApkService.getAlls(page, size, search);
    }

    @GetMapping("/file-path")
    @PreAuthorize("hasAnyAuthority('ROLE_Parent', 'ROLE_Children')")
    @Operation(summary = "Get robot apk file path by apk id and account id")
    public String getFilePathByApkId(@RequestParam("apkId")  UUID apkId, @RequestParam("accountId") UUID accountId) {
        return robotApkService.getFilePathByApkId(apkId, accountId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create new robot apk")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public RobotApkDto create(@RequestPart("robotApk") RobotApkDto dto, @RequestPart("file") MultipartFile file) {
        return robotApkService.create(dto, file);
    }

    @PutMapping(value = "/{apkId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update robot apk file")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public RobotApkDto updateApkFile(@PathVariable UUID apkId, @RequestPart("file") MultipartFile file) {
        return robotApkService.updateApkFile(apkId, file);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update robot apk")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public RobotApkDto update(@PathVariable UUID id, @RequestBody RobotApkDto robotApkDto) {
        return robotApkService.update(id, robotApkDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete robot apk")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public void delete(@PathVariable UUID id) {
        robotApkService.delete(id);
    }
}
