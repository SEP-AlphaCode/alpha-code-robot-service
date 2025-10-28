package com.alpha_code.alpha_code_robot_service.service;

public interface S3Service {
    String uploadBytes(byte[] data, String key, String contentType);
}

