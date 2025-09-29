package com.alpha_code.alpha_code_robot_service.controller;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotDto;
import com.alpha_code.alpha_code_robot_service.service.RobotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/robots")
@RequiredArgsConstructor
@Tag(name = "Robots")
@Validated
public class RobotController {
    private final RobotService service;

    @GetMapping
    @Operation(summary = "Get all robots")
    public PagedResult<RobotDto> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                        @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                        @RequestParam(value = "accountId", required = false) UUID accountId,
                                        @RequestParam(value = "status", required = false) Integer status,
                                        @RequestParam(value = "robotModelId", required = false) UUID robotModelId) {
        return service.getAll(page, size, serialNumber, accountId, status, robotModelId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get robot by id")
    public RobotDto getOne(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get all robots by account id")
    public List<RobotDto> getAllByAccountId(@PathVariable UUID accountId) {
        return service.getAllByAccountId(accountId);
    }

    @PostMapping
    @Operation(summary = "Create new robot")
    public RobotDto create(@RequestBody RobotDto robotDto) {
        return service.create(robotDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update robot")
    public RobotDto update(@PathVariable UUID id, @RequestBody RobotDto robotDto) {
        return  service.update(id, robotDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch robot")
    public RobotDto patch(@PathVariable UUID id, @RequestBody RobotDto robotDto) {
        return service.patch(id, robotDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete robot")
    public  String delete(@PathVariable UUID id) {
        return service.delete(id);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Change robot status")
    public RobotDto changeStatus(@PathVariable UUID id, @RequestBody Integer status) {
        return service.changeStatus(id, status);
    }
}
