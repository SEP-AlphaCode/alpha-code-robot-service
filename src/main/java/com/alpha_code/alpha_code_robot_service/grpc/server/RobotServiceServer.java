package com.alpha_code.alpha_code_robot_service.grpc.server;

import com.alpha_code.alpha_code_robot_service.service.RobotModelService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import robot.Robot;
import robot.RobotServiceGrpc;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class RobotServiceServer extends RobotServiceGrpc.RobotServiceImplBase {
    private final RobotModelService robotModelService;

    @PostConstruct
    public void init() {
        log.info("RobotServiceGrpc initialized successfully");
    }

    @Override
    public void getRobotModel(Robot.GetByIdRequest request, StreamObserver<Robot.RobotModelInformation> responseObserver) {
        String requestId = request.getId();
        log.info("Received GetRobotModel request with ID: {}", requestId);

        try {
            UUID robotModelId = UUID.fromString(requestId);
            var robotModel = robotModelService.getById(robotModelId);

            if (robotModel == null) {
                log.warn("Robot model not found for id={}", requestId);
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Robot model not found")
                        .asRuntimeException());
                return;
            }

            var response = Robot.RobotModelInformation.newBuilder()
                    .setId(robotModel.getId().toString())
                    .setName(robotModel.getName())
                    .setCtrlVersion(robotModel.getCtrlVersion())
                    .setFirmwareVersion(robotModel.getFirmwareVersion())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            log.error("Invalid id format: {}", requestId, e);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid id format")
                    .withCause(e)
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Internal error while getting robot model for id={}", requestId, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getRobotModelsByIds(Robot.GetByIdsRequest request,
                                    StreamObserver<Robot.RobotModelListResponse> responseObserver) {
        List<String> ids = request.getIdsList();
        log.info("Received GetRobotModelsByIds request with {} IDs", ids.size());

        try {
            List<UUID> uuidList = ids.stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList());

            // Giả sử bạn có hàm trong service: findAllByIds(List<UUID>)
            var robotModels = robotModelService.findAllByIds(uuidList);

            var response = Robot.RobotModelListResponse.newBuilder()
                    .addAllModels(robotModels.stream()
                            .map(model -> Robot.RobotModelInformation.newBuilder()
                                    .setId(model.getId().toString())
                                    .setName(model.getName())
                                    .setCtrlVersion(model.getCtrlVersion())
                                    .setFirmwareVersion(model.getFirmwareVersion())
                                    .build())
                            .toList())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            log.error("Invalid ID format in list: {}", ids, e);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid ID format in list")
                    .withCause(e)
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Internal error while getting robot models list", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}
