package com.alpha_code.alpha_code_robot_service.controller;

import com.alpha_code.alpha_code_robot_service.dto.CommandMapperDto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.service.CommandMapperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/command-mappers")
@RequiredArgsConstructor
@Tag(name = "Command Mappers")
@Validated
public class CommandMapperController {

    private final CommandMapperService service;

    @GetMapping
    @Operation(summary = "Get all command mappers")
    public PagedResult<CommandMapperDto> getAllCommandMappers(@RequestParam(value = "page", defaultValue = "1") int page,
                                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                                              @RequestParam(value = "activityId", required = false) UUID activityId,
                                                              @RequestParam(value = "status", required = false) Integer status,
                                                              @RequestParam(value = "commandId", required = false) UUID commandId) {
        return service.searchCommandMappers(page, size, activityId, status, commandId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get command mapper by id")
    public CommandMapperDto getCommandMapperById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/command-name/{commandName}")
    @Operation(summary = "Get command mapper by command name")
    public List<CommandMapperDto> getCommandMapperByCommandName(@PathVariable String commandName) {
        return service.getByCommandName(commandName);
    }

    @PostMapping
    @Operation(summary = "Create command mapper")
    public CommandMapperDto createCommandMapper(@RequestBody CommandMapperDto commandMapperDto) {
        return service.create(commandMapperDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update command mapper")
    public CommandMapperDto updateCommandMapper(@PathVariable UUID id, @RequestBody CommandMapperDto commandMapperDto) {
        return service.update(id, commandMapperDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch command mapper")
    public CommandMapperDto patchCommandMapper(@PathVariable UUID id, @RequestBody CommandMapperDto commandMapperDto) {
        return service.patch(id, commandMapperDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete command mapper")
    public String deleteCommandMapper(@PathVariable UUID id) {
        return service.delete(id);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Patch command mapper status")
    public CommandMapperDto patchCommandMapperStatus(@PathVariable UUID id, @RequestParam Integer status) {
        return service.changeStatus(id, status);
    }
}
