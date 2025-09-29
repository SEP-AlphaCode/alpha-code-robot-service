package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.CommandMapperDto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.entity.CommandMapper;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.mapper.CommandMapperMapper;
import com.alpha_code.alpha_code_robot_service.repository.CommandMapperRepository;
import com.alpha_code.alpha_code_robot_service.repository.CommandRepository;
import com.alpha_code.alpha_code_robot_service.service.CommandMapperService;
import com.alpha_code.alpha_code_robot_service.service.CommandService;

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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommandMapperServiceImpl implements CommandMapperService {

    private final CommandMapperRepository repository;
    private final CommandRepository commandRepository;

    @Override
    @Cacheable(value = "command_mappers_list", key = "{#page, #size, #activityId, #status, #commandId}")
    public PagedResult<CommandMapperDto> searchCommandMappers(int page, int size, UUID activityId, Integer status, UUID commandId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CommandMapper> pagedResult;

        pagedResult = repository.searchCommandMappers(activityId, status, commandId, pageable);

        return new PagedResult<>(pagedResult.map(CommandMapperMapper::toDto));
    }

    @Override
    @Cacheable(value = "command_mapper", key = "#id")
    public CommandMapperDto getById(UUID id) {
        var commandMapper = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommandMapper not found"));
        return CommandMapperMapper.toDto(commandMapper);
    }

    @Override
    @Cacheable(value = "command_mappers_by_command_name", key = "#commandName")
    public List<CommandMapperDto> getByCommandName(String commandName) {
        var command = commandRepository.getCommandByNameIgnoreCaseAndStatusNot(commandName, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Command not found"));

        var commandMappers = repository.getCommandMappersByCommandIdAndStatusNot(command.getId(), 0);

        if (commandMappers.isEmpty()) {
            throw new ResourceNotFoundException("CommandMapper not found");
        }

        return commandMappers.stream()
                .map(CommandMapperMapper::toDto)
                .toList();
    }


    @Override
    @Transactional
    @CacheEvict(value = "command_mappers_list", allEntries = true)
    public CommandMapperDto create(CommandMapperDto commandMapperDto) {
        var commandMapper = CommandMapperMapper.toEntity(commandMapperDto);
        commandMapper.setCreatedDate(LocalDateTime.now());

        CommandMapper savedCommandMapper = repository.save(commandMapper);
        return CommandMapperMapper.toDto(savedCommandMapper);
    }

    @Override
    @Transactional
    @CacheEvict(value = "command_mappers_list", allEntries = true)
    @CachePut(value = "command_mapper", key = "#id")
    public CommandMapperDto update(UUID id, CommandMapperDto commandMapperDto) {
        var commandMapper = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommandMapper not found"));

        commandMapper.setActivityId(commandMapperDto.getActivityId());
        commandMapper.setStatus(commandMapperDto.getStatus());
        commandMapper.setCommandId(commandMapperDto.getCommandId());
        commandMapper.setLastUpdated(LocalDateTime.now());

        CommandMapper savedCommandMapper = repository.save(commandMapper);
        return CommandMapperMapper.toDto(savedCommandMapper);
    }

    @Override
    @Transactional
    @CacheEvict(value = "command_mappers_list", allEntries = true)
    @CachePut(value = "command_mapper", key = "#id")
    public CommandMapperDto patch(UUID id, CommandMapperDto commandMapperDto) {
        var commandMapper = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommandMapper not found"));

        if (commandMapperDto.getActivityId() != null) {
            commandMapper.setActivityId(commandMapperDto.getActivityId());
        }
        if (commandMapperDto.getStatus() != null) {
            commandMapper.setStatus(commandMapperDto.getStatus());
        }
        if (commandMapperDto.getCommandId() != null) {
            commandMapper.setCommandId(commandMapperDto.getCommandId());
        }
        commandMapper.setLastUpdated(LocalDateTime.now());

        CommandMapper savedCommandMapper = repository.save(commandMapper);
        return CommandMapperMapper.toDto(savedCommandMapper);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"command_mappers_list", "command_mapper"}, allEntries = true)
    public String delete(UUID id) {
        var commandMapper = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommandMapper not found"));

        commandMapper.setStatus(0);
        commandMapper.setLastUpdated(LocalDateTime.now());

        repository.save(commandMapper);
        return "CommandMapper deleted successfully";
    }

    @Override
    @Transactional
    @CacheEvict(value = "command_mappers_list", allEntries = true)
    @CachePut(value = "command_mapper", key = "#id")
    public CommandMapperDto changeStatus(UUID id, Integer status) {
        var commandMapper = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommandMapper not found"));

        commandMapper.setStatus(status);
        commandMapper.setLastUpdated(LocalDateTime.now());

        CommandMapper savedCommandMapper = repository.save(commandMapper);
        return CommandMapperMapper.toDto(savedCommandMapper);
    }
}
