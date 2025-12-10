package com.alpha_code.alpha_code_robot_service.service.impl;

import com.alpha_code.alpha_code_robot_service.dto.PagedResult;
import com.alpha_code.alpha_code_robot_service.dto.VideoCaptureDto;
import com.alpha_code.alpha_code_robot_service.entity.VideoCapture;
import com.alpha_code.alpha_code_robot_service.enums.VideoCaptureEnum;
import com.alpha_code.alpha_code_robot_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_robot_service.mapper.VideoCaptureMapper;
import com.alpha_code.alpha_code_robot_service.repository.VideoCaptureRepository;
import com.alpha_code.alpha_code_robot_service.service.VideoCaptureService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoCaptureServiceImpl implements VideoCaptureService {

    private final VideoCaptureRepository videoCaptureRepository;
    private final ObjectMapper objectMapper;

    @Value("${python.api.url}")
    private String videoApiUrl;

    @Override
    public PagedResult<VideoCaptureDto> getAll(int page, int size, UUID accountId, Integer status) {
        log.info("Getting all video captures - page: {}, size: {}, accountId: {}, status: {}", page, size, accountId, status);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<VideoCapture> pagedResult = videoCaptureRepository.searchVideoCaptures(accountId, status, pageable);

        return new PagedResult<>(pagedResult.map(VideoCaptureMapper::toDto));
    }

    @Override
    public VideoCaptureDto getById(UUID videoCaptureId) {
        log.info("Getting video capture by ID: {}", videoCaptureId);

        VideoCapture videoCapture = videoCaptureRepository.findById(videoCaptureId)
                .orElseThrow(() -> new ResourceNotFoundException("Video capture not found with id: " + videoCaptureId));

        return VideoCaptureMapper.toDto(videoCapture);
    }

    @Override
    @Transactional
    public VideoCaptureDto createVideoCapture(VideoCaptureDto videoCaptureDto) {
        log.info("Creating video capture for account: {}", videoCaptureDto.getAccountId());

        VideoCapture videoCapture = VideoCaptureMapper.toEntity(videoCaptureDto);
        videoCapture.setCreatedDate(LocalDateTime.now());
        videoCapture.setStatus(VideoCaptureEnum.ACTIVE.getCode());
        videoCapture.setIsCreated(false);

        VideoCapture savedVideoCapture = videoCaptureRepository.save(videoCapture);
        log.info("Video capture created with ID: {}", savedVideoCapture.getId());

        return VideoCaptureMapper.toDto(savedVideoCapture);
    }

    @Override
    @Transactional
    public VideoCaptureDto generateVideoCapture(UUID videoCaptureId, String description) {
        log.info("Generating video for capture ID: {}", videoCaptureId);

        // Lấy video capture từ database
        VideoCapture videoCapture = videoCaptureRepository.findById(videoCaptureId)
                .orElseThrow(() -> new ResourceNotFoundException("Video capture not found with id: " + videoCaptureId));

        // Kiểm tra trạng thái
        if (videoCapture.getStatus() != VideoCaptureEnum.ACTIVE.getCode()) {
            throw new IllegalStateException("Video capture is not in active status");
        }

        try {
            // Gọi API để generate video
            String videoUrl = callVideoGenerationApi(videoCapture.getImage(), description);

            // Cập nhật video capture
            videoCapture.setVideoUrl(videoUrl);
            videoCapture.setDescription(description);
            videoCapture.setIsCreated(true);
            videoCapture.setLastUpdated(LocalDateTime.now());

            VideoCapture updatedVideoCapture = videoCaptureRepository.save(videoCapture);
            log.info("Video generated successfully for capture ID: {}", videoCaptureId);

            return VideoCaptureMapper.toDto(updatedVideoCapture);

        } catch (Exception e) {
            log.error("Error generating video for capture ID: {}", videoCaptureId, e);
            throw new RuntimeException("Failed to generate video: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteVideoCapture(UUID videoCaptureId) {
        log.info("Deleting video capture with ID: {}", videoCaptureId);

        VideoCapture videoCapture = videoCaptureRepository.findById(videoCaptureId)
                .orElseThrow(() -> new ResourceNotFoundException("Video capture not found with id: " + videoCaptureId));

        // Soft delete: chỉ cập nhật status
        videoCapture.setStatus(VideoCaptureEnum.DELETED.getCode());
        videoCapture.setLastUpdated(LocalDateTime.now());

        videoCaptureRepository.save(videoCapture);
        log.info("Video capture deleted (soft delete) with ID: {}", videoCaptureId);
    }

    /**
     * Gọi API để generate video từ image và description
     * API endpoint expects multipart/form-data with file upload
     */
    private String callVideoGenerationApi(String imageUrl, String description) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Download image từ imageUrl
            log.info("Downloading image from URL: {}", imageUrl);
            byte[] imageBytes = downloadImageFromUrl(imageUrl, restTemplate);
            log.info("Image downloaded successfully, size: {} bytes", imageBytes.length);

            // Chuẩn bị headers cho multipart/form-data
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Chuẩn bị multipart request body
            org.springframework.util.MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();

            // Add file part
            org.springframework.core.io.ByteArrayResource fileResource = new org.springframework.core.io.ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return "image.jpg"; // Tên file
                }
            };
            body.add("file", fileResource);

            // Add description part
            body.add("description", description != null ? description : "");

            // Add use_default_template part (khuyến nghị sử dụng template mặc định)
            body.add("use_default_template", "true");

            HttpEntity<org.springframework.util.MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            // Gọi API
            String apiEndpoint = videoApiUrl + "/generate";
            log.info("Calling video generation API at: {}", apiEndpoint);
            log.info("Description: {}", description);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                // API trả về structure: { "message": "...", "data": { "video_url": "...", ... } }
                JsonNode dataNode = jsonNode.path("data");
                String videoUrl = dataNode.path("video_url").asText();

                if (videoUrl == null || videoUrl.isEmpty()) {
                    log.error("No video URL in response. Response body: {}", response.getBody());
                    throw new RuntimeException("No video URL returned from API");
                }

                log.info("Video generated successfully: {}", videoUrl);
                return videoUrl;
            } else {
                log.error("API returned error status: {}. Response: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("API returned error status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error calling video generation API", e);
            throw new RuntimeException("Failed to call video generation API: " + e.getMessage(), e);
        }
    }

    /**
     * Download image từ URL
     */
    private byte[] downloadImageFromUrl(String imageUrl, RestTemplate restTemplate) {
        try {
            log.info("Downloading image from: {}", imageUrl);
            ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to download image from URL: " + imageUrl);
            }
        } catch (Exception e) {
            log.error("Error downloading image from URL: {}", imageUrl, e);
            throw new RuntimeException("Failed to download image: " + e.getMessage(), e);
        }
    }

}
