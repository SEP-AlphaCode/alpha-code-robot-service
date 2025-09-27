package com.alpha_code.alpha_code_robot_service.service;

import com.alpha_code.alpha_code_robot_service.dto.Esp32Dto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface Esp32Service {
    PagedResult<Esp32Dto> searchAll(
            int page,
            int size,
            UUID accountId,
            String macAddress,
            String name,
            Integer firmwareVersion,
            String topicPub,
            String topicSub,
            Integer status
    );

    Esp32Dto getOne(UUID id);

    Esp32Dto create(Esp32Dto dto);

    Esp32Dto update(UUID id, Esp32Dto dto);

    Esp32Dto patch(UUID id, Esp32Dto dto);

    String delete(UUID id);

    Esp32Dto changeStatus(UUID id, Integer status);

    Esp32Dto sendMessage(UUID id, String message) throws MqttException;
}
