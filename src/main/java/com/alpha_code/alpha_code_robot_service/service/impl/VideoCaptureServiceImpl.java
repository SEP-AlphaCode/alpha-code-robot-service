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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoCaptureServiceImpl implements VideoCaptureService {

    private final VideoCaptureRepository videoCaptureRepository;
    private final ObjectMapper objectMapper;

    @Value("${video.api.key}")
    private String videoApiKey;

    @Value("${video.api.url}")
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
     */
    private String callVideoGenerationApi(String imageUrl, String description) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Chuẩn bị headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(videoApiKey);

            // Chuẩn bị request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("image_url", imageUrl);
            requestBody.put("prompt", description);
            requestBody.put("duration", 5); // video duration in seconds

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Gọi API
            log.info("Calling video generation API at: {}", videoApiUrl + "/generate");
            ResponseEntity<String> response = restTemplate.exchange(
                    videoApiUrl + "/generate",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String videoUrl = jsonNode.path("video_url").asText();

                if (videoUrl == null || videoUrl.isEmpty()) {
                    // Nếu không có video_url ngay, có thể cần poll status
                    String taskId = jsonNode.path("task_id").asText();
                    if (taskId != null && !taskId.isEmpty()) {
                        videoUrl = pollVideoGenerationStatus(taskId, restTemplate, headers);
                    } else {
                        throw new RuntimeException("No video URL or task ID in response");
                    }
                }

                log.info("Video generated successfully: {}", videoUrl);
                return videoUrl;
            } else {
                throw new RuntimeException("API returned error status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error calling video generation API", e);
            throw new RuntimeException("Failed to call video generation API: " + e.getMessage(), e);
        }
    }

    /**
     * Poll API để kiểm tra trạng thái generation (nếu API không trả video URL ngay)
     */
    private String pollVideoGenerationStatus(String taskId, RestTemplate restTemplate, HttpHeaders headers) {
        try {
            int maxAttempts = 60; // Tối đa 60 lần (5 phút với mỗi lần 5 giây)
            int attempt = 0;

            while (attempt < maxAttempts) {
                Thread.sleep(5000); // Đợi 5 giây giữa các lần poll

                HttpEntity<Void> request = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        videoApiUrl + "/status/" + taskId,
                        HttpMethod.GET,
                        request,
                        String.class
                );

                if (response.getStatusCode() == HttpStatus.OK) {
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    String status = jsonNode.path("status").asText();

                    if ("completed".equalsIgnoreCase(status)) {
                        String videoUrl = jsonNode.path("video_url").asText();
                        log.info("Video generation completed: {}", videoUrl);
                        return videoUrl;
                    } else if ("failed".equalsIgnoreCase(status)) {
                        throw new RuntimeException("Video generation failed");
                    }
                    // Nếu status là "processing", tiếp tục poll
                }

                attempt++;
            }

            throw new RuntimeException("Video generation timeout");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Polling interrupted", e);
        } catch (Exception e) {
            throw new RuntimeException("Error polling video generation status", e);
        }
    }
}
