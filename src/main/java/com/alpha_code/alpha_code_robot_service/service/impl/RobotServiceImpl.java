package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotDto;
import com.alpha_code.alpha_code_robot_service.entity.Robot;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.mapper.RobotMapper;
import com.alpha_code.alpha_code_robot_service.repository.RobotRepository;
import com.alpha_code.alpha_code_robot_service.service.RobotService;
import lombok.RequiredArgsConstructor;
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
public class RobotServiceImpl implements RobotService {

    private final RobotRepository repository;

    @Override
    @Cacheable(value = "robots_list", key = "{#page, #size, #serialNumber, #accountId, #status, #robotModelId}")
    public PagedResult<RobotDto> getAll(int page, int size, String serialNumber, UUID accountId, Integer status, UUID robotModelId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Robot> pagedResult;

        pagedResult = repository.searchRobots(serialNumber,accountId,status,robotModelId,pageable);

        return new PagedResult<>(pagedResult.map(RobotMapper::toDto));
    }

    @Override
    @Cacheable(value = "robot", key = "#id")
    public RobotDto getById(UUID id) {
        var robot = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot not found"));

        return RobotMapper.toDto(robot);
    }

    @Override
    @Transactional
    @CacheEvict(value = "robots_list", allEntries = true)
    public RobotDto create(RobotDto robotDto) {
        var exist = repository.findRobotBySerialNumber(robotDto.getSerialNumber());
        if(exist.isPresent()){
            throw new RuntimeException("Robot already exists");
        }

        Robot robot = RobotMapper.toEntity(robotDto);
        robot.setCreatedDate(LocalDateTime.now());

        Robot savedRobot = repository.save(robot);
        return RobotMapper.toDto(savedRobot);
    }

    @Override
    @Transactional
    @CacheEvict(value = "robots_list", allEntries = true)
    @CachePut(value = "robot", key = "#id")
    public RobotDto update(UUID id, RobotDto robotDto) {
        var exist = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot not found"));

        exist.setSerialNumber(robotDto.getSerialNumber());
        exist.setAccountId(robotDto.getAccountId());
        exist.setRobotModelId(robotDto.getRobotModelId());
        exist.setStatus(robotDto.getStatus());
        exist.setLastUpdated(LocalDateTime.now());

        Robot savedRobot = repository.save(exist);
        return RobotMapper.toDto(savedRobot);
    }

    @Override
    @Transactional
    @CacheEvict(value = "robots_list", allEntries = true)
    @CachePut(value = "robot", key = "#id")
    public RobotDto patch(UUID id, RobotDto robotDto) {
        var exist = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot not found"));


        if (robotDto.getSerialNumber() != null) {
            exist.setSerialNumber(robotDto.getSerialNumber());
        }
        if (robotDto.getAccountId() != null) {
            exist.setAccountId(robotDto.getAccountId());
        }
        if (robotDto.getRobotModelId() != null) {
            exist.setRobotModelId(robotDto.getRobotModelId());
        }
        if (robotDto.getStatus() != null) {
            exist.setStatus(robotDto.getStatus());
        }

        exist.setLastUpdated(LocalDateTime.now());

        Robot savedRobot = repository.save(exist);
        return RobotMapper.toDto(savedRobot);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"robots_list", "robot"}, allEntries = true)
    public String delete(UUID id) {
        var exist = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot not found"));

        exist.setStatus(0);
        exist.setLastUpdated(LocalDateTime.now());

        repository.save(exist);
        return "Robot deleted successfully";
    }

    @Override
    @Transactional
    @CacheEvict(value = "robots_list", allEntries = true)
    @CachePut(value = "robot", key = "#id")
    public RobotDto changeStatus(UUID id, Integer status) {
        var exist = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot not found"));

        exist.setStatus(status);
        exist.setLastUpdated(LocalDateTime.now());

        Robot savedRobot = repository.save(exist);
        return RobotMapper.toDto(savedRobot);
    }
}
