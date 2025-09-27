package com.alpha_code.alpha_code_robot_service.mapper;

import com.alpha_code.alpha_code_robot_service.dto.Esp32Dto;
import com.alpha_code.alpha_code_robot_service.entity.Esp32;

public class Esp32Mapper {
    public static Esp32Dto toDto(Esp32 esp32) {
        if (esp32 == null) return null;

        Esp32Dto dto = new Esp32Dto();
        dto.setId(esp32.getId());
        dto.setAccountId(esp32.getAccountId());
        dto.setMacAddress(esp32.getMacAddress());
        dto.setName(esp32.getName());
        dto.setLastSeen(esp32.getLastSeen());
        dto.setFirmwareVersion(esp32.getFirmwareVersion());
        dto.setMetadata(esp32.getMetadata());
        dto.setTopicPub(esp32.getTopicPub());
        dto.setTopicSub(esp32.getTopicSub());
        dto.setMessage(esp32.getMessage());
        dto.setCreatedAt(esp32.getCreatedAt());
        dto.setLastUpdated(esp32.getLastUpdated());
        dto.setStatus(esp32.getStatus());
        return dto;
    }

    public static Esp32 toEntity(Esp32Dto dto) {
        if (dto == null) return null;

        Esp32 esp32 = new Esp32();
        esp32.setId(dto.getId());
        esp32.setAccountId(dto.getAccountId());
        esp32.setMacAddress(dto.getMacAddress());
        esp32.setName(dto.getName());
        esp32.setLastSeen(dto.getLastSeen());
        esp32.setFirmwareVersion(dto.getFirmwareVersion());
        esp32.setMetadata(dto.getMetadata());
        esp32.setTopicPub(dto.getTopicPub());
        esp32.setTopicSub(dto.getTopicSub());
        esp32.setMessage(dto.getMessage());
        esp32.setCreatedAt(dto.getCreatedAt());
        esp32.setLastUpdated(dto.getLastUpdated());
        esp32.setStatus(dto.getStatus());
        return esp32;
    }
}
