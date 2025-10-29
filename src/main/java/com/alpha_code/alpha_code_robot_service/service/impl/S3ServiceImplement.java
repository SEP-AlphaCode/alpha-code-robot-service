package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3ServiceImplement implements S3Service {

    private final S3Client s3Client;
    private final Region awsRegion;

    @Value("${application.bucket.name}")
    private String bucketName;

    @Override
    public String uploadBytes(byte[] data, String key, String contentType) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(data)
        );

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                awsRegion.id(),
                key
        );
    }

    @Override
    public String uploadStream(MultipartFile file, String key) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                awsRegion.id(),
                key
        );
    }

}
