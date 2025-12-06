package com.alpha_code.alpha_code_robot_service.service;

import com.alpha_code.alpha_code_robot_service.dto.Esp32Dto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.response.VoiceResponse;
import com.alpha_code.alpha_code_robot_service.entity.Esp32;
import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface Esp32Service {
    PagedResult<Esp32Dto> searchAll(
            int page,
            int size,
            UUID accountId,
            String name,
            Integer firmwareVersion,
            Integer status
    );

    Esp32Dto getOne(UUID id);

    Esp32Dto create(Esp32Dto dto);

    Esp32Dto update(UUID id, Esp32Dto dto);

    Esp32Dto patch(UUID id, Esp32Dto dto);

    String delete(UUID id);

    Esp32Dto changeStatus(UUID id, Integer status);

    VoiceResponse sendMessage(UUID id, String name, String message, String language) throws MqttException;

    List<JsonNode> getDevices(UUID id);

    boolean deviceExists(UUID id, String name);

    Esp32Dto addDevice(UUID id, String name, String type);

    Esp32Dto removeDevice(UUID id, String name);

    Esp32Dto updateDevice(UUID id, String name, String newName, String newType);

    Esp32Dto getEsp32ByUser(UUID id);
}
