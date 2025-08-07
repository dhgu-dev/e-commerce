package com.loopers.interfaces.api.orders;

import com.loopers.application.orders.dto.OrderInfo;
import com.loopers.application.orders.dto.OrderItemInfo;

import java.math.BigDecimal;
import java.util.List;

public class OrderV1Dto {
    public record OrderResponse(
        Long id, Long memberId, BigDecimal totalPrice, String status, List<OrderItemResponse> items
    ) {
        public static OrderResponse from(
            OrderInfo orderInfo
        ) {
            return new OrderResponse(
                orderInfo.id(),
                orderInfo.memberId(),
                orderInfo.totalPrice(),
                orderInfo.status(),
                orderInfo.items().stream().map(OrderItemResponse::from).toList()
            );
        }

        public record OrderItemResponse(
            Long productId,
            String productName,
            BigDecimal price,
            Long quantity
        ) {
            public static OrderItemResponse from(
                OrderItemInfo item
            ) {
                return new OrderItemResponse(
                    item.productId(),
                    item.productName(),
                    item.price(),
                    item.quantity()
                );
            }
        }
    }

    public record OrderRequest(
        List<OrderItemRequest> items,
        Long couponId
    ) {
        public record OrderItemRequest(
            Long productId,
            Long quantity
        ) {
        }
    }
}
