package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotModelDto;
import com.alpha_code.alpha_code_robot_service.entity.RobotModel;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.repository.RobotModelRepository;
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
@DisplayName("RobotModelServiceImpl Tests")
class RobotModelServiceImplTest {

    @Mock
    private RobotModelRepository repository;

    @InjectMocks
    private RobotModelServiceImpl robotModelService;

    private RobotModel robotModel;
    private RobotModelDto robotModelDto;
    private UUID robotModelId;

    @BeforeEach
    void setUp() {
        robotModelId = UUID.randomUUID();

        robotModel = RobotModel.builder()
                .id(robotModelId)
                .name("AlphaMini")
                .firmwareVersion("1.0.0")
                .ctrlVersion("2.0.0")
                .robotPrompt("You are a helpful assistant")
                .status(1)
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        robotModelDto = new RobotModelDto();
        robotModelDto.setId(robotModelId);
        robotModelDto.setName("AlphaMini");
        robotModelDto.setFirmwareVersion("1.0.0");
        robotModelDto.setCtrlVersion("2.0.0");
        robotModelDto.setRobotPrompt("You are a helpful assistant");
        robotModelDto.setStatus(1);
    }

    @Test
    @DisplayName("Should find all robot models by ids")
    void testFindAllByIds() {
        // Given
        List<UUID> ids = List.of(robotModelId);
        when(repository.findAllByIdIn(ids)).thenReturn(List.of(robotModel));

        // When
        List<RobotModel> result = robotModelService.findAllByIds(ids);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findAllByIdIn(ids);
    }

    @Test
    @DisplayName("Should return empty list when ids is null")
    void testFindAllByIds_Null() {
        // When
        List<RobotModel> result = robotModelService.findAllByIds(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, never()).findAllByIdIn(any());
    }

    @Test
    @DisplayName("Should return empty list when ids is empty")
    void testFindAllByIds_Empty() {
        // When
        List<RobotModel> result = robotModelService.findAllByIds(List.of());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, never()).findAllByIdIn(any());
    }

    @Test
    @DisplayName("Should get robot model by id successfully")
    void testGetById_Success() {
        // Given
        when(repository.findById(robotModelId)).thenReturn(Optional.of(robotModel));

        // When
        RobotModelDto result = robotModelService.getById(robotModelId);

        // Then
        assertNotNull(result);
        assertEquals(robotModelId, result.getId());
        assertEquals("AlphaMini", result.getName());
        verify(repository, times(1)).findById(robotModelId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when robot model not found")
    void testGetById_NotFound() {
        // Given
        when(repository.findById(robotModelId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> robotModelService.getById(robotModelId));
        verify(repository, times(1)).findById(robotModelId);
    }

    @Test
    @DisplayName("Should create robot model successfully")
    void testCreate_Success() {
        // Given
        when(repository.save(any(RobotModel.class))).thenReturn(robotModel);

        // When
        RobotModelDto result = robotModelService.create(robotModelDto);

        // Then
        assertNotNull(result);
        assertEquals(robotModelId, result.getId());
        verify(repository, times(1)).save(any(RobotModel.class));
    }

    @Test
    @DisplayName("Should update robot model successfully")
    void testUpdate_Success() {
        // Given
        RobotModelDto updateDto = new RobotModelDto();
        updateDto.setName("AlphaMini Pro");
        updateDto.setFirmwareVersion("1.1.0");
        updateDto.setCtrlVersion("2.1.0");
        updateDto.setRobotPrompt("Updated prompt");
        updateDto.setStatus(2);

        when(repository.findById(robotModelId)).thenReturn(Optional.of(robotModel));
        when(repository.save(any(RobotModel.class))).thenReturn(robotModel);

        // When
        RobotModelDto result = robotModelService.update(robotModelId, updateDto);

        // Then
        assertNotNull(result);
        verify(repository, times(1)).findById(robotModelId);
        verify(repository, times(1)).save(any(RobotModel.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when robot model not found in update")
    void testUpdate_NotFound() {
        // Given
        when(repository.findById(robotModelId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> robotModelService.update(robotModelId, robotModelDto));
        verify(repository, times(1)).findById(robotModelId);
        verify(repository, never()).save(any(RobotModel.class));
    }

    @Test
    @DisplayName("Should patch robot model successfully with partial update")
    void testPatch_Success() {
        // Given
        RobotModelDto patchDto = new RobotModelDto();
        patchDto.setName("AlphaMini Pro");
        patchDto.setStatus(2);

        when(repository.findById(robotModelId)).thenReturn(Optional.of(robotModel));
        when(repository.save(any(RobotModel.class))).thenReturn(robotModel);

        // When
        RobotModelDto result = robotModelService.patch(robotModelId, patchDto);

        // Then
        assertNotNull(result);
        verify(repository, times(1)).findById(robotModelId);
        verify(repository, times(1)).save(any(RobotModel.class));
    }

    @Test
    @DisplayName("Should patch robot model with null values")
    void testPatch_WithNullValues() {
        // Given
        RobotModelDto patchDto = new RobotModelDto();
        patchDto.setName(null);
        patchDto.setFirmwareVersion(null);

        when(repository.findById(robotModelId)).thenReturn(Optional.of(robotModel));
        when(repository.save(any(RobotModel.class))).thenReturn(robotModel);

        // When
        RobotModelDto result = robotModelService.patch(robotModelId, patchDto);

        // Then
        assertNotNull(result);
        verify(repository, times(1)).findById(robotModelId);
        verify(repository, times(1)).save(any(RobotModel.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when robot model not found in patch")
    void testPatch_NotFound() {
        // Given
        when(repository.findById(robotModelId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> robotModelService.patch(robotModelId, robotModelDto));
        verify(repository, times(1)).findById(robotModelId);
        verify(repository, never()).save(any(RobotModel.class));
    }

    @Test
    @DisplayName("Should delete robot model successfully")
    void testDelete_Success() {
        // Given
        when(repository.findById(robotModelId)).thenReturn(Optional.of(robotModel));
        when(repository.save(any(RobotModel.class))).thenReturn(robotModel);

        // When
        String result = robotModelService.delete(robotModelId);

        // Then
        assertEquals("Robot model deleted successfully", result);
        verify(repository, times(1)).findById(robotModelId);
        verify(repository, times(1)).save(any(RobotModel.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when robot model not found in delete")
    void testDelete_NotFound() {
        // Given
        when(repository.findById(robotModelId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> robotModelService.delete(robotModelId));
        verify(repository, times(1)).findById(robotModelId);
        verify(repository, never()).save(any(RobotModel.class));
    }

    @Test
    @DisplayName("Should change status successfully")
    void testChangeStatus_Success() {
        // Given
        Integer newStatus = 2;
        when(repository.findById(robotModelId)).thenReturn(Optional.of(robotModel));
        when(repository.save(any(RobotModel.class))).thenReturn(robotModel);

        // When
        RobotModelDto result = robotModelService.changeStatus(robotModelId, newStatus);

        // Then
        assertNotNull(result);
        verify(repository, times(1)).findById(robotModelId);
        verify(repository, times(1)).save(any(RobotModel.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when robot model not found in changeStatus")
    void testChangeStatus_NotFound() {
        // Given
        when(repository.findById(robotModelId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> robotModelService.changeStatus(robotModelId, 2));
        verify(repository, times(1)).findById(robotModelId);
        verify(repository, never()).save(any(RobotModel.class));
    }
}

