package com.alpha_code.alpha_code_robot_service.controller;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotCommandDto;
import com.alpha_code.alpha_code_robot_service.service.RobotCommandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/robot-commands")
@RequiredArgsConstructor
@Tag(name = "Robot Commands")
@Validated
public class RobotCommandController {

    private final RobotCommandService service;

    @GetMapping
    public PagedResult<RobotCommandDto> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                               @RequestParam(value = "robotModelId", required = false) UUID robotModelId,
                                               @RequestParam(value = "commandId", required = false) UUID commandId,
                                               @RequestParam(value = "status", required = false) Integer status) {
        return service.getAll(page, size, robotModelId, commandId, status);
    }

    @GetMapping("/name")
    public PagedResult<RobotCommandDto> getAllByName(@RequestParam(value = "page", defaultValue = "1") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                     @RequestParam(value = "robotModelName", required = false) String robotModelName,
                                                     @RequestParam(value = "commandName", required = false) String commandName,
                                                     @RequestParam(value = "status", required = false) Integer status) {
        return service.getAllByRobotModelNameAndCommandName(page, size, robotModelName, commandName, status);
    }

    @GetMapping("/{id}")
    public RobotCommandDto getOne(@PathVariable UUID id) {
        return service.getOne(id);
    }

    @PostMapping
    public RobotCommandDto create(@RequestBody RobotCommandDto robotCommandDto) {
        return service.create(robotCommandDto);
    }

    @PutMapping("/{id}")
    public RobotCommandDto update(@PathVariable UUID id, @RequestBody RobotCommandDto robotCommandDto) {
        return service.update(id, robotCommandDto);
    }

    @PatchMapping("/{id}")
    public RobotCommandDto patch(@PathVariable UUID id, @RequestBody RobotCommandDto robotCommandDto) {
        return service.patch(id, robotCommandDto);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id) {
        return service.delete(id);
    }

    @PatchMapping("/{id}/change-status")
    public RobotCommandDto changeStatus(@PathVariable UUID id, @RequestBody Integer status) {
        return service.changeStatus(id, status);
    }
}
