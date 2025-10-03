package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.CommandMapperDto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.entity.CommandMapper;
import com.alpha_code.alpha_code_robot_service.exception.ConflictException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command Mapper"));
        return CommandMapperMapper.toDto(commandMapper);
    }

    @Override
    @Cacheable(value = "command_mappers_by_command_name", key = "#commandName")
    public List<CommandMapperDto> getByCommandName(String commandName) {
        var command = commandRepository.getCommandByNameIgnoreCaseAndStatusNot(commandName, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command"));

        var commandMappers = repository.getCommandMappersByCommandIdAndStatusNot(command.getId(), 0);

        if (commandMappers.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy Command Mapper");
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
        int count = 0;
        if (commandMapper.getActivityId() != null) {
            count++;
        }
        if (commandMapper.getActionId() != null) {
            count++;
        }
        if (commandMapper.getExpressionId() != null) {
            count++;
        }
        if (commandMapper.getDanceId() != null) {
            count++;
        }
        if (count == 0) {
            throw new ConflictException("ActivityId, ActionId, ExpressionId, DanceId không được để trống");
        }
        if (count > 1) {
            throw new ConflictException("Chỉ được tồn tại 1 trong ActivityId, ActionId, ExpressionId, DanceId");
        }


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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command Mapper"));
        int count = 0;
        if (commandMapper.getActivityId() != null) {
            count++;
        }
        if (commandMapper.getActionId() != null) {
            count++;
        }
        if (commandMapper.getExpressionId() != null) {
            count++;
        }
        if (commandMapper.getDanceId() != null) {
            count++;
        }
        if (count > 1) {
            throw new ConflictException("Chỉ được tồn tại 1 trong ActivityId, ActionId, ExpressionId, DanceId");
        }

        if (commandMapperDto.getActivityId() != null) {
            commandMapper.setActivityId(commandMapperDto.getActivityId());
            commandMapper.setActionId(null);
            commandMapper.setExpressionId(null);
            commandMapper.setDanceId(null);
        }
        if (commandMapperDto.getActionId() != null) {
            commandMapper.setActionId(commandMapperDto.getActionId());
            commandMapper.setActivityId(null);
            commandMapper.setExpressionId(null);
            commandMapper.setDanceId(null);
        }
        if (commandMapperDto.getExpressionId() != null) {
            commandMapper.setExpressionId(commandMapperDto.getExpressionId());
            commandMapper.setActivityId(null);
            commandMapper.setActionId(null);
            commandMapper.setDanceId(null);
        }
        if (commandMapperDto.getDanceId() != null) {
            commandMapper.setDanceId(commandMapperDto.getDanceId());
            commandMapper.setActivityId(null);
            commandMapper.setActionId(null);
            commandMapper.setExpressionId(null);
        }


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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command Mapper"));
        int count = 0;
        if (commandMapper.getActivityId() != null) {
            count++;
        }
        if (commandMapper.getActionId() != null) {
            count++;
        }
        if (commandMapper.getExpressionId() != null) {
            count++;
        }
        if (commandMapper.getDanceId() != null) {
            count++;
        }
        if (count > 1) {
            throw new ConflictException("Chỉ được tồn tại 1 trong ActivityId, ActionId, ExpressionId, DanceId");
        }

        if (commandMapperDto.getActivityId() != null) {
            commandMapper.setActivityId(commandMapperDto.getActivityId());
            commandMapper.setActionId(null);
            commandMapper.setExpressionId(null);
            commandMapper.setDanceId(null);
        }
        if (commandMapperDto.getActionId() != null) {
            commandMapper.setActionId(commandMapperDto.getActionId());
            commandMapper.setActivityId(null);
            commandMapper.setExpressionId(null);
            commandMapper.setDanceId(null);
        }
        if (commandMapperDto.getExpressionId() != null) {
            commandMapper.setExpressionId(commandMapperDto.getExpressionId());
            commandMapper.setActivityId(null);
            commandMapper.setActionId(null);
            commandMapper.setDanceId(null);
        }
        if (commandMapperDto.getDanceId() != null) {
            commandMapper.setDanceId(commandMapperDto.getDanceId());
            commandMapper.setActivityId(null);
            commandMapper.setActionId(null);
            commandMapper.setExpressionId(null);
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command Mapper"));

        commandMapper.setStatus(0);
        commandMapper.setLastUpdated(LocalDateTime.now());

        repository.save(commandMapper);
        return "Command Mapper đã xóa thành công";
    }

    @Override
    @Transactional
    @CacheEvict(value = "command_mappers_list", allEntries = true)
    @CachePut(value = "command_mapper", key = "#id")
    public CommandMapperDto changeStatus(UUID id, Integer status) {
        var commandMapper = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command Mapper"));

        commandMapper.setStatus(status);
        commandMapper.setLastUpdated(LocalDateTime.now());

        CommandMapper savedCommandMapper = repository.save(commandMapper);
        return CommandMapperMapper.toDto(savedCommandMapper);
    }
}
