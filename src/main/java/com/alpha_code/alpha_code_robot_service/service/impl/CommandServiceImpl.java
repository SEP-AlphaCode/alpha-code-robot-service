package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.CommandDto;
import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.entity.Command;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.mapper.CommandMapper;
import com.alpha_code.alpha_code_robot_service.repository.CommandRepository;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommandServiceImpl implements CommandService {

    private final CommandRepository repository;

    @Override
    @Cacheable(value = "commands_list", key = "{#page, #size, #name, #status}")
    public PagedResult<CommandDto> getAll(int page, int size, String name, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Command> pagedResult;

//        if (name != null && status != null) {
//            pagedResult = repository.getCommandsByStatusAndNameContaining(status, name, pageable);
//        } else if (name != null) {
//            pagedResult = repository.getCommandsByNameContaining(name, pageable);
//        } else if (status != null) {
//            pagedResult = repository.getCommandsByStatus(status, pageable);
//        } else {
//            pagedResult = repository.findAll(pageable);
//        }

        pagedResult = repository.searchAll(name, status, pageable);

        return new PagedResult<>(pagedResult.map(CommandMapper::toDto));

    }

    @Override
    @Cacheable(value = "command", key = "#id")
    public CommandDto getOne(UUID id) {
        var command = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command"));

        return  CommandMapper.toDto(command);
    }

    @Override
    @Cacheable(value = "command", key = "#name")
    public CommandDto getByName(String name) {
        var command = repository.getCommandByNameIgnoreCaseAndStatusNot(name, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command"));

        return CommandMapper.toDto(command);
    }

    @Override
    @Transactional
    @CacheEvict(value = "commands_list", allEntries = true)
    public CommandDto create(CommandDto commandDto) {
        var exist = repository.getCommandByNameIgnoreCaseAndStatusNot(commandDto.getName(), 0);

        if (exist.isPresent()) {
            throw new ResourceNotFoundException("Tên Command đã tồn tại");
        }

        var command = repository.save(CommandMapper.toEntity(commandDto));
        command.setCreatedDate(LocalDateTime.now());

        Command savedCommand = repository.save(command);
        return CommandMapper.toDto(savedCommand);
    }

    @Override
    @Transactional
    @CacheEvict(value = "commands_list", allEntries = true)
    @CachePut(value = "command", key = "#id")
    public CommandDto update(UUID id, CommandDto commandDto) {
        var command = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command"));

        var exist = repository.getCommandByNameIgnoreCaseAndStatusNot(commandDto.getName(), 0);

        if (exist.isPresent()) {
            throw new ResourceNotFoundException("Tên Command đã tồn tại");
        }

        command.setName(commandDto.getName());
        command.setDescription(commandDto.getDescription());
        command.setStatus(commandDto.getStatus());
        command.setLastUpdated(LocalDateTime.now());

        Command savedCommand = repository.save(command);
        return CommandMapper.toDto(savedCommand);
    }

    @Override
    @Transactional
    @CacheEvict(value = "commands_list", allEntries = true)
    @CachePut(value = "command", key = "#id")
    public CommandDto patch(UUID id, CommandDto commandDto) {
        var command = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command"));

        var exist = repository.getCommandByNameIgnoreCaseAndStatusNot(commandDto.getName(), 0);

        if (exist.isPresent()) {
            throw new ResourceNotFoundException("Tên Command đã tồn tại");
        }

        if (commandDto.getName() != null) {
            command.setName(commandDto.getName());
        }
        if (commandDto.getDescription() != null) {
            command.setDescription(commandDto.getDescription());
        }
        if (commandDto.getStatus() != null) {
            command.setStatus(commandDto.getStatus());
        }
        command.setLastUpdated(LocalDateTime.now());

        Command savedCommand = repository.save(command);
        return CommandMapper.toDto(savedCommand);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"commands_list", "command"}, allEntries = true)
    public String delete(UUID id) {
        var command = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command"));

        command.setStatus(0);
        command.setLastUpdated(LocalDateTime.now());

        repository.save(command);
        return "Command đã được xóa";
    }

    @Override
    @Transactional
    @CacheEvict(value = {"commands_list"}, allEntries = true)
    @CachePut(value = "command", key = "#id")
    public CommandDto changeStatus(UUID id, Integer status) {
        var command = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Command"));

        command.setStatus(status);
        command.setLastUpdated(LocalDateTime.now());

        Command savedCommand = repository.save(command);
        return  CommandMapper.toDto(savedCommand);
    }
}
