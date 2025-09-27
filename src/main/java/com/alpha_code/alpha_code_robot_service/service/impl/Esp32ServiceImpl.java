package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.Esp32Dto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.entity.Esp32;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.mapper.Esp32Mapper;
import com.alpha_code.alpha_code_robot_service.repository.Esp32Repository;
import com.alpha_code.alpha_code_robot_service.service.Esp32Service;
import com.alpha_code.alpha_code_robot_service.service.MqttService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class Esp32ServiceImpl implements Esp32Service {

    private final Esp32Repository repository;
    private final MqttService mqttService;

    @Override
    @Cacheable(value = "esp32_list", key = "{#page, #size, #accountId, #macAddress, #name, #firmwareVersion, #topicPub, #topicSub, #status}")
    public PagedResult<Esp32Dto> searchAll(int page,
                                           int size,
                                           UUID accountId,
                                           String macAddress,
                                           String name,
                                           Integer firmwareVersion,
                                           String topicPub,
                                           String topicSub,
                                           Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Esp32> pagedResult;

        pagedResult = repository.searchAll(accountId, macAddress, name, firmwareVersion,
                topicPub, topicSub, status, pageable);

        return new PagedResult<>(pagedResult.map(Esp32Mapper::toDto));
    }

    @Override
    @Cacheable(value = "esp32", key = "#id")
    public Esp32Dto getOne(UUID id) {
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esp32 not found"));
        return Esp32Mapper.toDto(esp32);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"esp32_list"}, allEntries = true)
    public Esp32Dto create(Esp32Dto dto) {
        var esp32 = Esp32Mapper.toEntity(dto);
        esp32.setCreatedAt(LocalDateTime.now());
        Esp32 savedEntity = repository.save(esp32);
        return Esp32Mapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"esp32_list"}, allEntries = true)
    @CachePut(value = "esp32", key = "#id")
    public Esp32Dto update(UUID id, Esp32Dto dto) {
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esp32 not found"));

        esp32.setAccountId(dto.getAccountId());
        esp32.setMacAddress(dto.getMacAddress());
        esp32.setName(dto.getName());
        esp32.setLastSeen(dto.getLastSeen());
        esp32.setFirmwareVersion(dto.getFirmwareVersion());
        esp32.setMetadata(dto.getMetadata());
        esp32.setTopicPub(dto.getTopicPub());
        esp32.setTopicSub(dto.getTopicSub());
        esp32.setMessage(dto.getMessage());
        esp32.setLastUpdated(LocalDateTime.now());
        esp32.setStatus(dto.getStatus());

        Esp32 savedEntity = repository.save(esp32);
        return Esp32Mapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"esp32_list"}, allEntries = true)
    @CachePut(value = "esp32", key = "#id")
    public Esp32Dto patch(UUID id, Esp32Dto dto) {
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esp32 not found"));

        if (dto.getAccountId() != null) {
            esp32.setAccountId(dto.getAccountId());
        }
        if (dto.getMacAddress() != null) {
            esp32.setMacAddress(dto.getMacAddress());
        }
        if (dto.getName() != null) {
            esp32.setName(dto.getName());
        }
        if (dto.getLastSeen() != null) {
            esp32.setLastSeen(dto.getLastSeen());
        }
        if (dto.getFirmwareVersion() != null) {
            esp32.setFirmwareVersion(dto.getFirmwareVersion());
        }
        if (dto.getMetadata() != null) {
            esp32.setMetadata(dto.getMetadata());
        }
        if (dto.getTopicPub() != null) {
            esp32.setTopicPub(dto.getTopicPub());
        }
        if (dto.getTopicSub() != null) {
            esp32.setTopicSub(dto.getTopicSub());
        }
        if (dto.getMessage() != null) {
            esp32.setMessage(dto.getMessage());
        }
        if (dto.getStatus() != null) {
            esp32.setStatus(dto.getStatus());
        }

        esp32.setLastUpdated(LocalDateTime.now());

        Esp32 savedEntity = repository.save(esp32);
        return Esp32Mapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"esp32_list", "esp32"}, allEntries = true)
    public String delete(UUID id) {
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esp32 not found"));

        esp32.setStatus(0);
        esp32.setLastUpdated(LocalDateTime.now());

        repository.save(esp32);
        return "Esp32 deleted successfully";
    }

    @Override
    @Transactional
    @CacheEvict(value = {"esp32_list"}, allEntries = true)
    @CachePut(value = "esp32", key = "#id")
    public Esp32Dto changeStatus(UUID id, Integer status) {
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esp32 not found"));

        esp32.setStatus(status);
        esp32.setLastUpdated(LocalDateTime.now());

        Esp32 savedEntity = repository.save(esp32);
        return Esp32Mapper.toDto(savedEntity);
    }

    @PostConstruct
    public void init() {
        // Subscribe các topicSub trong DB khi service khởi chạy
        repository.findAll().forEach(esp32 -> {
            mqttService.subscribe(esp32.getTopicSub(), (topic, payload) -> {
                log.info("📥 ESP32[{}] -> {}", esp32.getId(), payload);

                esp32.setMessage(payload);
                esp32.setLastSeen(LocalDateTime.now());
                repository.save(esp32);
            });
        });
    }


    @Override
    public Esp32Dto sendMessage(UUID id, String message) throws MqttException {
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esp32 not found"));

        mqttService.publish(esp32.getTopicPub(), message);
        return Esp32Mapper.toDto(esp32);
    }
}
