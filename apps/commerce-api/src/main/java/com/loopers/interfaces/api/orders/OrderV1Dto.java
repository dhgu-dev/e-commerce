package com.loopers.interfaces.api.orders;

import com.loopers.application.orders.dto.CardInfo;
import com.loopers.application.orders.dto.OrderInfo;
import com.loopers.application.orders.dto.OrderItemInfo;
import com.loopers.application.payment.dto.PaymentOrder;
import com.loopers.application.payment.dto.PaymentOrderResult;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.util.List;

public class OrderV1Dto {
    public record OrderResponse(
        Long id, Long memberId, BigDecimal totalPrice, String status, List<OrderItemResponse> items,
        PaymentOrderResult paymentOrderResult
    ) {
        public static OrderResponse from(
            OrderInfo orderInfo,
            PaymentOrderResult paymentOrderResult
        ) {
            return new OrderResponse(
                orderInfo.id(),
                orderInfo.memberId(),
                orderInfo.totalPrice(),
                orderInfo.status(),
                orderInfo.items().stream().map(OrderItemResponse::from).toList(),
                paymentOrderResult
            );
        }

        public static OrderResponse from(
            OrderInfo orderInfo
        ) {
            return new OrderResponse(
                orderInfo.id(),
                orderInfo.memberId(),
                orderInfo.totalPrice(),
                orderInfo.status(),
                orderInfo.items().stream().map(OrderItemResponse::from).toList(),
                null
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
        Long couponId,
        @Nullable CardDto card
    ) {
        public record OrderItemRequest(
            Long productId,
            Long quantity
        ) {
        }

        public record CardDto(
            CardTypeDto cardType,
            String cardNumber
        ) {
            public CardInfo toInfo() {
                return new CardInfo(
                    this.cardType == CardTypeDto.SAMSUNG ? CardInfo.CardTypeInfo.SAMSUNG :
                        this.cardType == CardTypeDto.KB ? CardInfo.CardTypeInfo.KB :
                            CardInfo.CardTypeInfo.HYUNDAI,
                    this.cardNumber
                );
            }

            public enum CardTypeDto {
                SAMSUNG,
                KB,
                HYUNDAI;

                public PaymentOrder.CardTypeInfo toInfo() {
                    return switch (this) {
                        case SAMSUNG -> PaymentOrder.CardTypeInfo.SAMSUNG;
                        case KB -> PaymentOrder.CardTypeInfo.KB;
                        case HYUNDAI -> PaymentOrder.CardTypeInfo.HYUNDAI;
                    };
                }
            }
        }
    }
}
