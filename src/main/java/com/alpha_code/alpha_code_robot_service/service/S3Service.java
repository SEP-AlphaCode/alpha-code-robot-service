package com.alpha_code.alpha_code_robot_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadBytes(byte[] data, String key, String contentType);
    String uploadStream(MultipartFile file, String key) throws Exception;
}

