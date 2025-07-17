package com.loopers.interfaces.api.points;

import com.loopers.application.points.PointsFacade;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointsV1Controller implements PointsV1ApiSpec {

    private final PointsFacade pointsFacade;

    @PostMapping("/charge")
    @Override
    public ApiResponse<PointsV1Dto.PointsResponse> charge(String userId, PointsV1Dto.PointsChargeRequest dto) {
        Long currentPoints = pointsFacade.chargePoints(userId, dto.amount());
        return ApiResponse.success(new PointsV1Dto.PointsResponse(currentPoints));
    }

    @GetMapping()
    @Override
    public ApiResponse<PointsV1Dto.PointsResponse> getMyPoints(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST);
        }
        return ApiResponse.success(new PointsV1Dto.PointsResponse(pointsFacade.getPoints(userId)));
    }
}
