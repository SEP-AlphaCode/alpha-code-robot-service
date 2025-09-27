package com.alpha_code.alpha_code_robot_service.controller;

import com.alpha_code.alpha_code_robot_service.dto.CommandDto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.service.CommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/commands")
@RequiredArgsConstructor
@Tag(name = "Commands")
@Validated
public class CommandController {

    private final CommandService service;

    @GetMapping
    @Operation(summary = "Get all commands")
    public PagedResult<CommandDto> getAllCommands(@RequestParam(value = "page", defaultValue = "1") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                                  @RequestParam(value = "name", required = false) String name,
                                                  @RequestParam(value = "status", required = false) Integer status) {
        return service.getAll(page, size, name, status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get command by id")
    public CommandDto getCommandById(@PathVariable UUID id) {
        return service.getOne(id);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get command by name")
    public CommandDto getCommandByName(@PathVariable String name) {
        return service.getByName(name);
    }

    @PostMapping
    @Operation(summary = "Create command")
    public CommandDto createCommand(@RequestBody CommandDto commandDto) {
        return service.create(commandDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update command")
    public CommandDto updateCommand(@PathVariable UUID id, @RequestBody CommandDto commandDto) {
        return service.update(id, commandDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch command")
    public CommandDto patchCommand(@PathVariable UUID id, @RequestBody CommandDto commandDto) {
        return service.patch(id, commandDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete command")
    public String deleteCommand(@PathVariable UUID id) {
        return service.delete(id);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Patch command status")
    public CommandDto patchCommandStatus(@PathVariable UUID id, @RequestParam Integer status) {
        return service.changeStatus(id, status);
    }
}
