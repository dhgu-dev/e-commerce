package com.loopers.interfaces.api.points;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Points V1 API", description = "포인트 API 입니다")
public interface PointsV1ApiSpec {

    @Operation(
            summary = "포인트 충전",
            description = "사용자의 포인트를 충전합니다. 충전된 포인트의 총량을 응답으로 반환합니다."
    )
    ApiResponse<PointsV1Dto.PointsResponse> charge(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody PointsV1Dto.PointsChargeRequest dto
    );

    @Operation(
            summary = "보유 포인트 조회",
            description = "사용자의 보유 포인트를 조회합니다."
    )
    ApiResponse<PointsV1Dto.PointsResponse> getMyPoints(
            @RequestHeader(value = "X-USER-ID", required = false) String userId
    );
}
