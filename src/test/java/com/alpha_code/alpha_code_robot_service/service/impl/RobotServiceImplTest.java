package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotDto;
import com.alpha_code.alpha_code_robot_service.entity.Robot;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.repository.RobotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RobotServiceImpl Tests")
class RobotServiceImplTest {

    @Mock
    private RobotRepository repository;

    @InjectMocks
    private RobotServiceImpl robotService;

    private Robot robot;
    private RobotDto robotDto;
    private UUID robotId;
    private UUID accountId;
    private UUID robotModelId;

    @BeforeEach
    void setUp() {
        robotId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        robotModelId = UUID.randomUUID();

        robot = Robot.builder()
                .id(robotId)
                .serialNumber("ROBOT-001")
                .accountId(accountId)
                .robotModelId(robotModelId)
                .status(1)
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        robotDto = new RobotDto();
        robotDto.setId(robotId);
        robotDto.setSerialNumber("ROBOT-001");
        robotDto.setAccountId(accountId);
        robotDto.setRobotModelId(robotModelId);
        robotDto.setStatus(1);
    }

    @Test
    @DisplayName("Should get robot by id successfully")
    void testGetById_Success() {
        // Given
        when(repository.findById(robotId)).thenReturn(Optional.of(robot));

        // When
        RobotDto result = robotService.getById(robotId);

        // Then
        assertNotNull(result);
        assertEquals(robotId, result.getId());
        assertEquals("ROBOT-001", result.getSerialNumber());
        verify(repository, times(1)).findById(robotId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when robot not found")
    void testGetById_NotFound() {
        // Given
        when(repository.findById(robotId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> robotService.getById(robotId));
        verify(repository, times(1)).findById(robotId);
    }

    @Test
    @DisplayName("Should get all robots by account id")
    void testGetAllByAccountId() {
        // Given
        when(repository.getAllByAccountIdAndStatusNot(accountId, 0))
                .thenReturn(List.of(robot));

        // When
        List<RobotDto> result = robotService.getAllByAccountId(accountId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(robotId, result.get(0).getId());
        verify(repository, times(1)).getAllByAccountIdAndStatusNot(accountId, 0);
    }

    @Test
    @DisplayName("Should create robot successfully")
    void testCreate_Success() {
        // Given
        when(repository.findRobotBySerialNumberAndStatusNot("ROBOT-001", 0))
                .thenReturn(Optional.empty());
        when(repository.save(any(Robot.class))).thenReturn(robot);

        // When
        RobotDto result = robotService.create(robotDto);

        // Then
        assertNotNull(result);
        assertEquals(robotId, result.getId());
        verify(repository, times(1)).findRobotBySerialNumberAndStatusNot("ROBOT-001", 0);
        verify(repository, times(1)).save(any(Robot.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when robot already exists")
    void testCreate_AlreadyExists() {
        // Given
        when(repository.findRobotBySerialNumberAndStatusNot("ROBOT-001", 0))
                .thenReturn(Optional.of(robot));

        // When & Then
        assertThrows(RuntimeException.class, () -> robotService.create(robotDto));
        verify(repository, times(1)).findRobotBySerialNumberAndStatusNot("ROBOT-001", 0);
        verify(repository, never()).save(any(Robot.class));
    }

    @Test
    @DisplayName("Should update robot successfully")
    void testUpdate_Success() {
        // Given
        RobotDto updateDto = new RobotDto();
        updateDto.setSerialNumber("ROBOT-002");
        updateDto.setAccountId(accountId);
        updateDto.setRobotModelId(robotModelId);
        updateDto.setStatus(2);

        when(repository.findRobotBySerialNumberAndStatusNot("ROBOT-002", 0))
                .thenReturn(Optional.empty());
        when(repository.findById(robotId)).thenReturn(Optional.of(robot));
        when(repository.save(any(Robot.class))).thenReturn(robot);

        // When
        RobotDto result = robotService.update(robotId, updateDto);

        // Then
        assertNotNull(result);
        verify(repository, times(1)).findRobotBySerialNumberAndStatusNot("ROBOT-002", 0);
        verify(repository, times(1)).findById(robotId);
        verify(repository, times(1)).save(any(Robot.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when serial number already exists in update")
    void testUpdate_SerialNumberExists() {
        // Given
        Robot existingRobot = Robot.builder()
                .id(UUID.randomUUID())
                .serialNumber("ROBOT-002")
                .build();

        RobotDto updateDto = new RobotDto();
        updateDto.setSerialNumber("ROBOT-002");

        when(repository.findRobotBySerialNumberAndStatusNot("ROBOT-002", 0))
                .thenReturn(Optional.of(existingRobot));

        // When & Then
        assertThrows(RuntimeException.class, () -> robotService.update(robotId, updateDto));
        verify(repository, times(1)).findRobotBySerialNumberAndStatusNot("ROBOT-002", 0);
        verify(repository, never()).save(any(Robot.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when robot not found in update")
    void testUpdate_NotFound() {
        // Given
        when(repository.findRobotBySerialNumberAndStatusNot(anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(repository.findById(robotId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> robotService.update(robotId, robotDto));
        verify(repository, times(1)).findById(robotId);
        verify(repository, never()).save(any(Robot.class));
    }

    @Test
    @DisplayName("Should delete robot successfully")
    void testDelete_Success() {
        // Given
        when(repository.findById(robotId)).thenReturn(Optional.of(robot));
        when(repository.save(any(Robot.class))).thenReturn(robot);

        // When
        String result = robotService.delete(robotId);

        // Then
        assertEquals("Robot deleted successfully", result);
        verify(repository, times(1)).findById(robotId);
        verify(repository, times(1)).save(any(Robot.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when robot not found in delete")
    void testDelete_NotFound() {
        // Given
        when(repository.findById(robotId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> robotService.delete(robotId));
        verify(repository, times(1)).findById(robotId);
        verify(repository, never()).save(any(Robot.class));
    }

    @Test
    @DisplayName("Should change status successfully")
    void testChangeStatus_Success() {
        // Given
        Integer newStatus = 2;
        when(repository.findById(robotId)).thenReturn(Optional.of(robot));
        when(repository.save(any(Robot.class))).thenReturn(robot);

        // When
        RobotDto result = robotService.changeStatus(robotId, newStatus);

        // Then
        assertNotNull(result);
        verify(repository, times(1)).findById(robotId);
        verify(repository, times(1)).save(any(Robot.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when robot not found in changeStatus")
    void testChangeStatus_NotFound() {
        // Given
        when(repository.findById(robotId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> robotService.changeStatus(robotId, 2));
        verify(repository, times(1)).findById(robotId);
        verify(repository, never()).save(any(Robot.class));
    }
}

