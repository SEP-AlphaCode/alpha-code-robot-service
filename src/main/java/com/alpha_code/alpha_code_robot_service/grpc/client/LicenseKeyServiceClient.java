package com.alpha_code.alpha_code_robot_service.grpc.client;

import io.grpc.StatusRuntimeException;
import license_key.GetLicenseRequest;
import license_key.GetLicenseResponse;
import license_key.LicenseKeyServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class LicenseKeyServiceClient {

    @GrpcClient("alpha-payment-service")
    private LicenseKeyServiceGrpc.LicenseKeyServiceBlockingStub blockingStub;

    /**
     * Gọi sang license-key-service để kiểm tra license của account
     */
    public GetLicenseResponse getLicenseByAccountId(UUID accountId) {
        try {
            GetLicenseRequest request = GetLicenseRequest.newBuilder()
                    .setAccountId(accountId.toString())
                    .build();

            GetLicenseResponse response = blockingStub.getLicenseByAccountId(request);

            log.info("[gRPC] LicenseKeyService trả về: hasLicense={}, status={}, key={}",
                    response.getHasLicense(), response.getStatus(), response.getKey());

            return response;

        } catch (StatusRuntimeException e) {
            // gRPC exception: service không chạy, timeout, v.v.
            log.error("[gRPC] Lỗi khi gọi LicenseKeyService: {}", e.getStatus(), e);
            throw new RuntimeException("Không thể kết nối đến license-key-service", e);
        } catch (Exception e) {
            log.error("[gRPC] Lỗi không xác định: {}", e.getMessage(), e);
            throw e;
        }
    }
}
