package com.alpha_code.alpha_code_robot_service.controller;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.VideoCaptureDto;
import com.alpha_code.alpha_code_robot_service.service.VideoCaptureService;
import com.alpha_code.alpha_code_robot_service.validation.OnCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/video-captures")
@RequiredArgsConstructor
@Tag(name = "Video Captures")
@Validated
public class VideoCaptureController {

    private final VideoCaptureService service;

    @GetMapping
    @Operation(summary = "Get all video captures")
    public PagedResult<VideoCaptureDto> getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "accountId", required = false) UUID accountId,
            @RequestParam(value = "status", required = false) Integer status) {
        return service.getAll(page, size, accountId, status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get video capture by id")
    public VideoCaptureDto getOne(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create video capture")
    public VideoCaptureDto create(
            @Validated(OnCreate.class) @RequestBody VideoCaptureDto dto) {
        return service.createVideoCapture(dto);
    }

    @PostMapping("/{id}/generate")
    @Operation(summary = "Generate video from image and description")
    public VideoCaptureDto generateVideo(
            @PathVariable UUID id,
            @RequestBody String description) {
        return service.generateVideoCapture(id, description);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete video capture (soft delete)")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        service.deleteVideoCapture(id);
        return ResponseEntity.ok(Map.of("message", "Video capture deleted successfully"));
    }
}

