package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotCommandDto;
import com.alpha_code.alpha_code_robot_service.entity.RobotCommand;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.mapper.RobotCommandMapper;
import com.alpha_code.alpha_code_robot_service.repository.CommandRepository;
import com.alpha_code.alpha_code_robot_service.repository.RobotCommandRepository;
import com.alpha_code.alpha_code_robot_service.repository.RobotModelRepository;
import com.alpha_code.alpha_code_robot_service.service.RobotCommandService;
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
public class RobotCommandServiceImpl implements RobotCommandService {
    
    private final RobotCommandRepository repository;
    private final RobotModelRepository robotModelRepository;
    private final CommandRepository commandRepository;
    
    @Override
    @Cacheable(value = "robot_commands_list", key = "{#page, #size, #robotModelId, #commandId, #status}")
    public PagedResult<RobotCommandDto> getAll(int page, int size, UUID robotModelId, UUID commandId, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<RobotCommand> pagedResult;

        pagedResult = repository.getAll(robotModelId, commandId, status, pageable);

        return new PagedResult<>(pagedResult.map(RobotCommandMapper::toDto));
    }

    @Override
    @Cacheable(value = "robot_command", key = "#id")
    public RobotCommandDto getOne(UUID id) {
        var robotCommand = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RobotCommand not found"));

        return RobotCommandMapper.toDto(robotCommand);
    }

    @Override
    @Cacheable(value = "robot_commands_list", key = "{#page, #size, #robotModelName, #commandName, #status}")
    public PagedResult<RobotCommandDto> getAllByRobotModelNameAndCommandName(int page, int size, String robotModelName, String commandName, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<RobotCommand> pagedResult;
        
        var robotModel = robotModelRepository.getRobotModelByNameIgnoreCaseAndStatusNot(robotModelName, 0);
        var command = commandRepository.getCommandByNameIgnoreCaseAndStatusNot(commandName, 0);
        
        pagedResult = repository.getAll(robotModel.get().getId(), command.get().getId(), status, pageable);

        return new PagedResult<>(pagedResult.map(RobotCommandMapper::toDto));
    }

    @Override
    @Transactional
    @CacheEvict(value = "robot_commands_list", allEntries = true)
    public RobotCommandDto create(RobotCommandDto robotCommandDto) {
        var robotCommand = RobotCommandMapper.toEntity(robotCommandDto);
        robotCommand.setCreatedDate(LocalDateTime.now());

        RobotCommand savedRobotCommand = repository.save(robotCommand);

        return RobotCommandMapper.toDto(savedRobotCommand);
    }

    @Override
    @Transactional
    @CacheEvict(value = "robot_commands_list", allEntries = true)
    @CachePut(value = "robot_command", key = "#id")
    public RobotCommandDto update(UUID id, RobotCommandDto robotCommandDto) {
        var robotCommand = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RobotCommand not found"));
        
        robotCommand.setRobotModelId(robotCommandDto.getRobotModelId());
        robotCommand.setCommandId(robotCommandDto.getCommandId());
        robotCommand.setStatus(robotCommandDto.getStatus());
        robotCommand.setLastUpdated(LocalDateTime.now());

        RobotCommand savedRobotCommand = repository.save(robotCommand);

        return RobotCommandMapper.toDto(savedRobotCommand);
    }

    @Override
    @Transactional
    @CacheEvict(value = "robot_commands_list", allEntries = true)
    @CachePut(value = "robot_command", key = "#id")
    public RobotCommandDto patch(UUID id, RobotCommandDto robotCommandDto) {
        var robotCommand = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RobotCommand not found"));

        if (robotCommandDto.getRobotModelId() != null ){
            robotCommand.setRobotModelId(robotCommandDto.getRobotModelId());
        }
        if (robotCommandDto.getCommandId() != null ){
            robotCommand.setCommandId(robotCommandDto.getCommandId());
        }
        if (robotCommandDto.getStatus() != null ){
            robotCommand.setStatus(robotCommandDto.getStatus());
        }
        robotCommand.setLastUpdated(LocalDateTime.now());

        RobotCommand savedRobotCommand = repository.save(robotCommand);

        return RobotCommandMapper.toDto(savedRobotCommand);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"robot_commands_list", "robot_command"}, allEntries = true)
    public String delete(UUID id) {
        var robotCommand = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RobotCommand not found"));
        
        robotCommand.setStatus(0);
        robotCommand.setLastUpdated(LocalDateTime.now());
        
        repository.save(robotCommand);
        
        return "RobotCommand deleted successfully";
    }

    @Override
    @Transactional
    @CacheEvict(value = {"robot_commands_list"}, allEntries = true)
    @CachePut(value = "robot_command", key = "#id")
    public RobotCommandDto changeStatus(UUID id, Integer status) {
        var robotCommand = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RobotCommand not found"));
        
        robotCommand.setStatus(status);
        robotCommand.setLastUpdated(LocalDateTime.now());
        
        RobotCommand savedRobotCommand = repository.save(robotCommand);
        return  RobotCommandMapper.toDto(savedRobotCommand);
    }
}
