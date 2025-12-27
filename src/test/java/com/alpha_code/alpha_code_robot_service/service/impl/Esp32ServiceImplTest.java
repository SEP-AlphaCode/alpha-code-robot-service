package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.Esp32Dto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.response.VoiceResponse;
import com.alpha_code.alpha_code_robot_service.entity.Esp32;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.repository.Esp32Repository;
import com.alpha_code.alpha_code_robot_service.service.MqttService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Esp32ServiceImpl Tests")
class Esp32ServiceImplTest {

    @Mock
    private Esp32Repository repository;

    @Mock
    private MqttService mqttService;

    @InjectMocks
    private Esp32ServiceImpl esp32Service;

    private Esp32 esp32;
    private Esp32Dto esp32Dto;
    private UUID esp32Id;
    private UUID accountId;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        esp32Id = UUID.randomUUID();
        accountId = UUID.randomUUID();

        esp32 = Esp32.builder()
                .id(esp32Id)
                .accountId(accountId)
                .name("ESP32-001")
                .firmwareVersion(1)
                .status(1)
                .message("")
                .createdAt(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        esp32Dto = new Esp32Dto();
        esp32Dto.setId(esp32Id);
        esp32Dto.setAccountId(accountId);
        esp32Dto.setName("ESP32-001");
        esp32Dto.setFirmwareVersion(1);
        esp32Dto.setStatus(1);
        esp32Dto.setMessage("");
    }

    @Test
    @DisplayName("Should get ESP32 by id successfully")
    void testGetOne_Success() {
        // Given
        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));

        // When
        Esp32Dto result = esp32Service.getOne(esp32Id);

        // Then
        assertNotNull(result);
        assertEquals(esp32Id, result.getId());
        assertEquals("ESP32-001", result.getName());
        verify(repository, times(1)).findById(esp32Id);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ESP32 not found")
    void testGetOne_NotFound() {
        // Given
        when(repository.findById(esp32Id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> esp32Service.getOne(esp32Id));
        verify(repository, times(1)).findById(esp32Id);
    }

    @Test
    @DisplayName("Should create ESP32 successfully")
    void testCreate_Success() {
        // Given
        when(repository.findByAccountIdAndStatus(accountId, 1))
                .thenReturn(Optional.empty());
        when(repository.save(any(Esp32.class))).thenReturn(esp32);

        // When
        Esp32Dto result = esp32Service.create(esp32Dto);

        // Then
        assertNotNull(result);
        assertEquals(esp32Id, result.getId());
        verify(repository, times(1)).findByAccountIdAndStatus(accountId, 1);
        verify(repository, times(1)).save(any(Esp32.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when account already has active ESP32")
    void testCreate_AccountHasActiveEsp32() {
        // Given
        Esp32 existingEsp32 = Esp32.builder()
                .id(UUID.randomUUID())
                .accountId(accountId)
                .status(1)
                .build();

        when(repository.findByAccountIdAndStatus(accountId, 1))
                .thenReturn(Optional.of(existingEsp32));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> esp32Service.create(esp32Dto));
        verify(repository, times(1)).findByAccountIdAndStatus(accountId, 1);
        verify(repository, never()).save(any(Esp32.class));
    }

    @Test
    @DisplayName("Should update ESP32 successfully")
    void testUpdate_Success() {
        // Given
        Esp32Dto updateDto = new Esp32Dto();
        updateDto.setName("ESP32-002");
        updateDto.setAccountId(accountId);
        updateDto.setStatus(1);

        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));
        when(repository.findByAccountIdAndStatus(accountId, 1))
                .thenReturn(Optional.empty());
        when(repository.save(any(Esp32.class))).thenReturn(esp32);

        // When
        Esp32Dto result = esp32Service.update(esp32Id, updateDto);

        // Then
        assertNotNull(result);
        verify(repository, times(1)).findById(esp32Id);
        verify(repository, times(1)).save(any(Esp32.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ESP32 not found in update")
    void testUpdate_NotFound() {
        // Given
        when(repository.findById(esp32Id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> esp32Service.update(esp32Id, esp32Dto));
        verify(repository, times(1)).findById(esp32Id);
        verify(repository, never()).save(any(Esp32.class));
    }

    @Test
    @DisplayName("Should patch ESP32 successfully")
    void testPatch_Success() {
        // Given
        Esp32Dto patchDto = new Esp32Dto();
        patchDto.setStatus(2);

        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));
        when(repository.save(any(Esp32.class))).thenReturn(esp32);

        // When
        Esp32Dto result = esp32Service.patch(esp32Id, patchDto);

        // Then
        assertNotNull(result);
        verify(repository, times(1)).findById(esp32Id);
        verify(repository, times(1)).save(any(Esp32.class));
    }

    @Test
    @DisplayName("Should delete ESP32 successfully")
    void testDelete_Success() {
        // Given
        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));
        when(repository.save(any(Esp32.class))).thenReturn(esp32);

        // When
        String result = esp32Service.delete(esp32Id);

        // Then
        assertEquals("Esp32 deleted successfully", result);
        verify(repository, times(1)).findById(esp32Id);
        verify(repository, times(1)).save(any(Esp32.class));
    }

    @Test
    @DisplayName("Should send message successfully")
    void testSendMessage_Success() throws MqttException {
        // Given
        String deviceName = "light";
        String message = "on";
        String language = "vi";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode metadata = mapper.createObjectNode();
        ArrayNode devices = mapper.createArrayNode();
        ObjectNode device = mapper.createObjectNode();
        device.put("name", deviceName);
        device.put("type", "switch");
        devices.add(device);
        metadata.set("devices", devices);

        esp32.setMetadata(metadata);
        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));
        doNothing().when(mqttService).publish(anyString(), anyString());

        // When
        VoiceResponse result = esp32Service.sendMessage(esp32Id, deviceName, message, language);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getMessage());
        verify(repository, times(3)).findById(esp32Id);
        verify(mqttService, times(1)).publish(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ESP32 not found in sendMessage")
    void testSendMessage_NotFound() {
        // Given
        when(repository.findById(esp32Id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, 
                () -> esp32Service.sendMessage(esp32Id, "light", "on", "vi"));
        verify(repository, times(1)).findById(esp32Id);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when device does not exist")
    void testSendMessage_DeviceNotFound() {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode metadata = mapper.createObjectNode();
        ArrayNode devices = mapper.createArrayNode();
        metadata.set("devices", devices);
        esp32.setMetadata(metadata);

        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> esp32Service.sendMessage(esp32Id, "nonexistent", "on", "vi"));
        verify(repository, times(3)).findById(esp32Id);
    }

    @Test
    @DisplayName("Should get devices successfully")
    void testGetDevices_Success() {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode metadata = mapper.createObjectNode();
        ArrayNode devices = mapper.createArrayNode();
        ObjectNode device = mapper.createObjectNode();
        device.put("name", "light");
        device.put("type", "switch");
        devices.add(device);
        metadata.set("devices", devices);

        esp32.setMetadata(metadata);
        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));

        // When
        List<JsonNode> result = esp32Service.getDevices(esp32Id);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findById(esp32Id);
    }

    @Test
    @DisplayName("Should return empty list when metadata is null")
    void testGetDevices_NullMetadata() {
        // Given
        esp32.setMetadata(null);
        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));

        // When
        List<JsonNode> result = esp32Service.getDevices(esp32Id);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findById(esp32Id);
    }

    @Test
    @DisplayName("Should check device exists successfully")
    void testDeviceExists_Success() {
        // Given
        String deviceName = "light";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode metadata = mapper.createObjectNode();
        ArrayNode devices = mapper.createArrayNode();
        ObjectNode device = mapper.createObjectNode();
        device.put("name", deviceName);
        device.put("type", "switch");
        devices.add(device);
        metadata.set("devices", devices);

        esp32.setMetadata(metadata);
        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));

        // When
        boolean result = esp32Service.deviceExists(esp32Id, deviceName);

        // Then
        assertTrue(result);
        verify(repository, times(2)).findById(esp32Id);
    }

    @Test
    @DisplayName("Should add device successfully")
    void testAddDevice_Success() {
        // Given
        String deviceName = "light";
        String deviceType = "switch";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode metadata = mapper.createObjectNode();
        ArrayNode devices = mapper.createArrayNode();
        metadata.set("devices", devices);
        esp32.setMetadata(metadata);

        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));
        when(repository.save(any(Esp32.class))).thenReturn(esp32);

        // When
        Esp32Dto result = esp32Service.addDevice(esp32Id, deviceName, deviceType);

        // Then
        assertNotNull(result);
        verify(repository, times(3)).findById(esp32Id);
        verify(repository, times(1)).save(any(Esp32.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when device already exists")
    void testAddDevice_AlreadyExists() {
        // Given
        String deviceName = "light";
        String deviceType = "switch";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode metadata = mapper.createObjectNode();
        ArrayNode devices = mapper.createArrayNode();
        ObjectNode device = mapper.createObjectNode();
        device.put("name", deviceName);
        device.put("type", deviceType);
        devices.add(device);
        metadata.set("devices", devices);
        esp32.setMetadata(metadata);

        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> esp32Service.addDevice(esp32Id, deviceName, deviceType));
        verify(repository, times(3)).findById(esp32Id);
        verify(repository, never()).save(any(Esp32.class));
    }

    @Test
    @DisplayName("Should remove device successfully")
    void testRemoveDevice_Success() {
        // Given
        String deviceName = "light";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode metadata = mapper.createObjectNode();
        ArrayNode devices = mapper.createArrayNode();
        ObjectNode device = mapper.createObjectNode();
        device.put("name", deviceName);
        device.put("type", "switch");
        devices.add(device);
        metadata.set("devices", devices);
        esp32.setMetadata(metadata);

        when(repository.findById(esp32Id)).thenReturn(Optional.of(esp32));
        when(repository.save(any(Esp32.class))).thenReturn(esp32);

        // When
        Esp32Dto result = esp32Service.removeDevice(esp32Id, deviceName);

        // Then
        assertNotNull(result);
        verify(repository, times(1)).findById(esp32Id);
        verify(repository, times(1)).save(any(Esp32.class));
    }

    @Test
    @DisplayName("Should get ESP32 by user successfully")
    void testGetEsp32ByUser_Success() {
        // Given
        when(repository.findByAccountId(accountId)).thenReturn(Optional.of(esp32));

        // When
        Esp32Dto result = esp32Service.getEsp32ByUser(accountId);

        // Then
        assertNotNull(result);
        assertEquals(esp32Id, result.getId());
        verify(repository, times(1)).findByAccountId(accountId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ESP32 not found by user")
    void testGetEsp32ByUser_NotFound() {
        // Given
        when(repository.findByAccountId(accountId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> esp32Service.getEsp32ByUser(accountId));
        verify(repository, times(1)).findByAccountId(accountId);
    }
}

