package com.loopers.interfaces.api.points;

import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointsV1Controller implements PointsV1ApiSpec {

    @PostMapping("/charge")
    @Override
    public ApiResponse<PointsV1Dto.PointsResponse> charge(String userId, PointsV1Dto.PointsChargeRequest dto) {
        return ApiResponse.success(new PointsV1Dto.PointsResponse(dto.amount()));
    }
}
