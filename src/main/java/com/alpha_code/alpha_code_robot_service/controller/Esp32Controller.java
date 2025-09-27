package com.alpha_code.alpha_code_robot_service.controller;

import com.alpha_code.alpha_code_robot_service.dto.Esp32Dto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.service.Esp32Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/esp32s")
@RequiredArgsConstructor
@Tag(name = "Esp32s")
@Validated
public class Esp32Controller {

    private final Esp32Service service;

    @GetMapping
    public PagedResult<Esp32Dto> getAll (@RequestParam(value = "page", defaultValue = "1") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int size,
                                         @RequestParam(value = "accountId", required = false) UUID accountId,
                                         @RequestParam(value = "macAddress", required = false) String macAddress,
                                         @RequestParam(value = "name", required = false) String name,
                                         @RequestParam(value = "firmwareVersion", required = false) Integer firmwareVersion,
                                         @RequestParam(value = "topicPub", required = false) String topicPub,
                                         @RequestParam(value = "topicSub", required = false) String topicSub,
                                         @RequestParam(value = "status", required = false) Integer status){
        return service.searchAll(page, size, accountId, macAddress, name, firmwareVersion, topicPub, topicSub, status);
    }

    @GetMapping("/{id}")
    public Esp32Dto getOne(@PathVariable UUID id){
        return service.getOne(id);
    }

    @PostMapping
    public Esp32Dto create(@RequestBody Esp32Dto dto){
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public Esp32Dto update(@PathVariable UUID id, @RequestBody Esp32Dto dto){
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    public Esp32Dto patch(@PathVariable UUID id, @RequestBody Esp32Dto dto){
        return service.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id){
        return service.delete(id);
    }

    @PatchMapping("/{id}/change-status")
    public Esp32Dto patchStatus(@PathVariable UUID id, @RequestParam Integer status){
        return service.changeStatus(id, status);
    }

    @PostMapping("/{id}/send-message")
    public Esp32Dto sendMessage(@PathVariable UUID id, @RequestParam String message) throws MqttException {
        return service.sendMessage(id, message);
    }
}
