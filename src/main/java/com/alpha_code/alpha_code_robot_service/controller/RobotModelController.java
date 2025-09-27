package com.alpha_code.alpha_code_robot_service.controller;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotModelDto;
import com.alpha_code.alpha_code_robot_service.service.RobotModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/robot-models")
@RequiredArgsConstructor
@Tag(name = "Robot Models")
@Validated
public class RobotModelController {
    private final RobotModelService service;

    @GetMapping
    @Operation(summary = "Get all robot models")
    public PagedResult<RobotModelDto> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size,
                                             @RequestParam(value = "name", required = false) String name,
                                             @RequestParam(value = "firmwareVersion", required = false) String firmwareVersion,
                                             @RequestParam(value = "ctrlVersion", required = false) String ctrlVersion,
                                             @RequestParam(value = "status", required = false) Integer status) {
        return service.getAll(page, size, name, firmwareVersion, ctrlVersion, status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get robot model by id")
    public RobotModelDto getOne(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create robot model")
    public RobotModelDto create(@RequestBody RobotModelDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update robot model")
    public RobotModelDto update(@PathVariable UUID id, @RequestBody RobotModelDto dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch robot model")
    public RobotModelDto patch(@PathVariable UUID id, @RequestBody RobotModelDto dto) {
        return service.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete robot model")
    public String delete(@PathVariable UUID id) {
        return service.delete(id);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Change robot model status")
    public RobotModelDto changeStatus(@PathVariable UUID id, @RequestBody Integer status) {
        return service.changeStatus(id, status);
    }
}
