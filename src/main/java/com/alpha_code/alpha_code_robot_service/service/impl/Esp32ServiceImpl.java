package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.Esp32Dto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.response.VoiceResponse;
import com.alpha_code.alpha_code_robot_service.entity.Esp32;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.mapper.Esp32Mapper;
import com.alpha_code.alpha_code_robot_service.repository.Esp32Repository;
import com.alpha_code.alpha_code_robot_service.service.Esp32Service;
import com.alpha_code.alpha_code_robot_service.service.MqttService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.ArrayList;
import java.util.List;
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));

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
    public VoiceResponse sendMessage(UUID id, String name, String message, String language) throws MqttException {
        // 1. Lấy ESP32
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));

        // 2. Kiểm tra thiết bị tồn tại
        if (!deviceExists(id, name)) {
            throw new IllegalArgumentException("Thiết bị " + name + " không tồn tại");
        }

        // 3. Gửi lệnh MQTT
        mqttService.publish(id + "/" + name, message.toUpperCase());

        // 4. Chuẩn hóa message theo language
        String responseMessage = localizeMessage(message, language);

        // 5. Trả về message cho robot đọc
        return new VoiceResponse(true, responseMessage);
    }

    private String localizeMessage(String message, String language) {
        if ("en".equalsIgnoreCase(language)) {
            switch (message.toLowerCase()) {
                case "turn_on":
                    return "The device is turned on.";
                case "turn_off":
                    return "The device is turned off.";
                default:
                    return message; // fallback: trả nguyên message
            }
        } else if ("vi".equalsIgnoreCase(language)) {
            switch (message.toLowerCase()) {
                case "turn_on":
                    return "Thiết bị đã được bật.";
                case "turn_off":
                    return "Thiết bị đã được tắt.";
                default:
                    return message; // fallback
            }
        }

        return message; // default fallback
    }



    @Override
    public List<JsonNode> getDevices(UUID id) {
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));
        JsonNode metadata = esp32.getMetadata();

        if (metadata == null || metadata.get("devices") == null)
            return List.of();

        JsonNode devices = metadata.get("devices");

        if (!devices.isArray())
            return List.of();

        List<JsonNode> list = new ArrayList<>();
        devices.forEach(list::add);

        return list;
    }

    @Override
    public boolean deviceExists(UUID id, String name) {
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));
        return getDevices(id).stream()
                .anyMatch(d -> d.get("name").asText().equalsIgnoreCase(name));
    }

    @Override
    public Esp32Dto addDevice(UUID id, String name, String type) {
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));
        ObjectMapper mapper = new ObjectMapper();

        // Nếu metadata null → tạo mới
        JsonNode root = esp32.getMetadata();
        if (root == null || root.isNull()) {
            root = mapper.createObjectNode();
            ((ObjectNode) root).putArray("devices");
        }

        ArrayNode devicesNode = (ArrayNode) root.get("devices");
        if (devicesNode == null) {
            devicesNode = mapper.createArrayNode();
            ((ObjectNode) root).set("devices", devicesNode);
        }

        // check trùng
        if (deviceExists(id, name)) {
            throw new IllegalArgumentException("Thiết bị " + name + " đã tồn tại");
        }

        // tạo device JSON object
        ObjectNode dev = mapper.createObjectNode();
        dev.put("name", name);
        dev.put("type", type);

        devicesNode.add(dev);

        esp32.setMetadata(root);
        esp32.setLastUpdated(LocalDateTime.now());

        Esp32 savedEntity = repository.save(esp32);
        return Esp32Mapper.toDto(savedEntity);
    }

    @Override
    public Esp32Dto removeDevice(UUID id, String name) {
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = esp32.getMetadata();
        if (root == null || root.get("devices") == null)
            return Esp32Mapper.toDto(esp32);

        ArrayNode devicesNode = (ArrayNode) root.get("devices");
        ArrayNode newList = mapper.createArrayNode();

        for (JsonNode device : devicesNode) {
            if (!device.get("name").asText().equalsIgnoreCase(name)) {
                newList.add(device);
            }
        }

        ((ObjectNode) root).set("devices", newList);

        esp32.setMetadata(root);
        esp32.setLastUpdated(LocalDateTime.now());

        Esp32 savedEntity = repository.save(esp32);
        return Esp32Mapper.toDto(savedEntity);
    }

    @Override
    public Esp32Dto updateDevice(UUID id, String name, String newType) {
        var esp32 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));
        JsonNode root = esp32.getMetadata();
        ArrayNode devicesNode = (ArrayNode) root.get("devices");

        for (JsonNode device : devicesNode) {
            if (device.get("name").asText().equalsIgnoreCase(name)) {
                ((ObjectNode) device).put("type", newType);
            }
        }

        esp32.setLastUpdated(LocalDateTime.now());
        Esp32 savedEntity = repository.save(esp32);
        return Esp32Mapper.toDto(savedEntity);
    }

    @Override
    public Esp32Dto getEsp32ByUser(UUID id) {
        var esp32 = repository.findByAccountId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ESP32"));
        return Esp32Mapper.toDto(esp32);
    }

}
