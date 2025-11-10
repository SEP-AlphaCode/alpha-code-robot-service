package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.RobotApkDto;
import com.alpha_code.alpha_code_robot_service.entity.RobotApk;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.grpc.client.LicenseKeyServiceClient;
import com.alpha_code.alpha_code_robot_service.mapper.RobotApkMapper;
import com.alpha_code.alpha_code_robot_service.repository.RobotApkRepository;
import com.alpha_code.alpha_code_robot_service.service.RobotApkService;
import com.alpha_code.alpha_code_robot_service.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RobotApkServiceImpl implements RobotApkService {
    private final RobotApkRepository robotApkRepository;
    private final LicenseKeyServiceClient licenseKeyServiceClient;
    private final S3Service s3Service;

    @Override
    @Cacheable(value = "robot_apks_list", key = "{#page, #size, #search}")
    public PagedResult<RobotApkDto> getAlls(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size);
        var results = robotApkRepository.getAlls(search, pageable);
        return new PagedResult<>(results.map(RobotApkMapper::toDto)); // Placeholder return statement
    }

    @Override
    @Cacheable(value = "robot_apk_file_path", key = "{#apkId, #accountId}" )
    public String getFilePathByApkId(UUID apkId, UUID accountId) {
        var apk = robotApkRepository.findById(apkId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy APK"));

        if(apk.getFile() == null || apk.getFile().isEmpty()){
            throw new ResourceNotFoundException("File APK trống");
        }

        if(apk.getIsRequireLicense()){
            if(accountId == null){
                throw new IllegalArgumentException("Tài khoản là bắt buộc để tải APK này");
            }
            var licenseKey = licenseKeyServiceClient.getLicenseByAccountId(accountId);
            if(licenseKey == null || licenseKey.getStatus() != 1  ){
                throw new ResourceNotFoundException("Tài khoản không có license hợp lệ để tải APK này");
            }
        }

        return apk.getFile();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"robot_apks_list", "robot_apk_file_path"}, allEntries = true)
    public RobotApkDto create(RobotApkDto dto, MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File .zip là bắt buộc");
            }
            if (!file.getOriginalFilename().toLowerCase().endsWith(".zip")) {
                throw new IllegalArgumentException("File phải có định dạng .zip");
            }

            var existingApk = robotApkRepository.findByNameAndVersion(dto.getName(), dto.getVersion());
            if (existingApk.isPresent()) {
                throw new IllegalArgumentException("Đã tồn tại APK với tên và phiên bản này");
            }

            String fileKey = "robot-apks/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String fileUrl = s3Service.uploadBytes(file.getBytes(), fileKey, file.getContentType());


            RobotApk robotApk = RobotApkMapper.toEntity(dto);
            robotApk.setStatus(1);
            robotApk.setCreatedDate(LocalDateTime.now());
            robotApk.setFile(fileUrl); // đường dẫn file .zip trên S3

            RobotApk saved = robotApkRepository.save(robotApk);

            return RobotApkMapper.toDto(saved);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo RobotApk", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"robot_apks_list", "robot_apk_file_path"}, allEntries = true)
    public RobotApkDto updateApkFile(UUID apkId, MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File .zip là bắt buộc");
            }
            if (!file.getOriginalFilename().toLowerCase().endsWith(".zip")) {
                throw new IllegalArgumentException("File phải có định dạng .zip");
            }

            RobotApk robotApk = robotApkRepository.findById(apkId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy APK"));

            String fileKey = "robot-apks/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String fileUrl = s3Service.uploadBytes(file.getBytes(), fileKey, file.getContentType());

            robotApk.setFile(fileUrl); // cập nhật đường dẫn file .zip trên S3
            robotApk.setLastUpdated(LocalDateTime.now());

            return RobotApkMapper.toDto(robotApkRepository.save(robotApk));

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật file RobotApk", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"robot_apks_list", "robot_apk_file_path"}, allEntries = true)
    public RobotApkDto update(UUID id, RobotApkDto robotApkDto) {
        var robotApk = robotApkRepository.findRobotApkById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot APK không tìm thấy"));

        String newName = robotApkDto.getName() != null ? robotApkDto.getName() : robotApk.getName();
        String newVersion = robotApkDto.getVersion() != null ? robotApkDto.getVersion() : robotApk.getVersion();

        // Check nếu tồn tại APK khác có cùng name + version
        var existingApk = robotApkRepository.findByNameAndVersion(newName, newVersion);
        if (existingApk.isPresent() && !existingApk.get().getId().equals(id)) {
            throw new IllegalArgumentException("Đã tồn tại APK với tên và phiên bản này");
        }

        // Cập nhật các trường
        robotApk.setName(newName);
        robotApk.setVersion(newVersion);

        robotApk.setDescription(robotApkDto.getDescription());
        robotApk.setIsRequireLicense(robotApkDto.getIsRequireLicense());
        robotApk.setStatus(robotApkDto.getStatus());
        robotApk.setLastUpdated(LocalDateTime.now());

        RobotApk updatedRobotApk = robotApkRepository.save(robotApk);
        return RobotApkMapper.toDto(updatedRobotApk);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"robot_apks_list", "robot_apk_file_path"}, allEntries = true)
    public void delete(UUID id) {
        var robotApk = robotApkRepository.findRobotApkById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot APK không tìm thấy"));

        robotApk.setStatus(0);
        robotApkRepository.save(robotApk);
    }

}
