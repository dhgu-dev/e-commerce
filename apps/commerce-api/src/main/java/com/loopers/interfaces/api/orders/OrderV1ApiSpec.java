package com.loopers.interfaces.api.orders;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Order V1 API")
public interface OrderV1ApiSpec {

    @Operation(summary = "주문 요청")
    ApiResponse<OrderV1Dto.OrderResponse> order(
        String userId,
        OrderV1Dto.OrderRequest dto
    );

    @Operation(summary = "유저의 주문 목록 조회")
    ApiResponse<List<OrderV1Dto.OrderResponse>> getOrders(
        String userId
    );

    @Operation(summary = "단일 주문 상세 조회")
    ApiResponse<OrderV1Dto.OrderResponse> getOrder(
        String userId,
        Long orderId
    );
}
