package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotModelDto;
import com.alpha_code.alpha_code_robot_service.entity.RobotModel;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.mapper.RobotModelMapper;
import com.alpha_code.alpha_code_robot_service.repository.RobotModelRepository;
import com.alpha_code.alpha_code_robot_service.service.RobotModelService;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RobotModelServiceImpl implements RobotModelService {

    private  final RobotModelRepository repository;

    @Override
    @Cacheable(value = "robot_models_list", key = "{#page, #size, #name, #firmwareVersion, #ctrlVersion, #status}")
    public PagedResult<RobotModelDto> getAll(int page, int size, String name, String firmwareVersion, String ctrlVersion, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<RobotModel> pagedResult;

        pagedResult = repository.searchRobotModels(name,firmwareVersion,ctrlVersion,status,pageable);

        return new PagedResult<>(pagedResult.map(RobotModelMapper::toDto));
    }

    public List<RobotModel> findAllByIds(List<UUID> uuidList) {
        if (uuidList == null || uuidList.isEmpty()) {
            return List.of();
        }
        return repository.findAllByIdIn(uuidList);
    }

    @Override
    @Cacheable(value = "robot_model", key = "#id")
    public RobotModelDto getById(UUID id) {
        var robotModel = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot model not found"));
        return RobotModelMapper.toDto(robotModel);
    }

    @Override
    @Transactional
    @CacheEvict(value = "robot_models_list", allEntries = true)
    public RobotModelDto create(RobotModelDto robotModelDto) {
        var robotModel = RobotModelMapper.toEntity(robotModelDto);
        robotModel.setCreatedDate(LocalDateTime.now());
        robotModel = repository.save(robotModel);
        return RobotModelMapper.toDto(robotModel);
    }

    @Override
    @Transactional
    @CacheEvict(value = "robot_models_list", allEntries = true)
    @CachePut(value = "robot_model", key = "#id")
    public RobotModelDto update(UUID id, RobotModelDto robotModelDto) {
        var robotModel = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot model not found"));

        robotModel.setName(robotModelDto.getName());
        robotModel.setFirmwareVersion(robotModelDto.getFirmwareVersion());
        robotModel.setCtrlVersion(robotModelDto.getCtrlVersion());
        robotModel.setRobotPrompt(robotModelDto.getRobotPrompt());
        robotModel.setStatus(robotModelDto.getStatus());
        robotModel.setLastUpdated(LocalDateTime.now());

        RobotModel updatedRobotModel = repository.save(robotModel);
        return RobotModelMapper.toDto(updatedRobotModel);
    }

    @Override
    @Transactional
    @CacheEvict(value = "robot_models_list", allEntries = true)
    @CachePut(value = "robot_model", key = "#id")
    public RobotModelDto patch(UUID id, RobotModelDto robotModelDto) {
        var robotModel = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot model not found"));

        if (robotModelDto.getName() != null){
            robotModel.setName(robotModelDto.getName());
        }

        if (robotModelDto.getFirmwareVersion() != null){
            robotModel.setFirmwareVersion(robotModelDto.getFirmwareVersion());
        }

        if (robotModelDto.getCtrlVersion() != null){
            robotModel.setCtrlVersion(robotModelDto.getCtrlVersion());
        }

        if (robotModelDto.getRobotPrompt() != null){
            robotModel.setRobotPrompt(robotModelDto.getRobotPrompt());
        }
        if (robotModelDto.getStatus() != null){
            robotModel.setStatus(robotModelDto.getStatus());
        }

        robotModel.setLastUpdated(LocalDateTime.now());

        RobotModel updatedRobotModel = repository.save(robotModel);
        return RobotModelMapper.toDto(updatedRobotModel);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"robot_models_list", "robot_model"}, allEntries = true)
    public String delete(UUID id) {
        var robotModel = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot model not found"));

        robotModel.setStatus(0);
        robotModel.setLastUpdated(LocalDateTime.now());

        repository.save(robotModel);
        return "Robot model deleted successfully";
    }

    @Override
    @Transactional
    @CacheEvict(value = "robot_models_list", allEntries = true)
    @CachePut(value = "robot_model", key = "#id")
    public RobotModelDto changeStatus(UUID id, Integer status) {
        var robotModel = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot model not found"));

        robotModel.setStatus(status);
        robotModel.setLastUpdated(LocalDateTime.now());

        RobotModel updatedRobotModel = repository.save(robotModel);
        return RobotModelMapper.toDto(updatedRobotModel);
    }
}
