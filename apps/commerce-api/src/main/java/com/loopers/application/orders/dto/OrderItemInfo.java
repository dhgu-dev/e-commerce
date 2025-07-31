package com.loopers.application.orders.dto;

import com.loopers.domain.orders.OrderItemModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.math.BigDecimal;

public record OrderItemInfo(Long productId, String productName, BigDecimal price, Long quantity) {

    public OrderItemInfo {
        if (productId == null || productName == null || productName.isBlank() || price == null || quantity == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "All fields must be provided");
        }
    }

    public static OrderItemInfo from(OrderItemModel item) {
        return new OrderItemInfo(
            item.getProductSnapshot().getProductId(),
            item.getProductSnapshot().getName(),
            item.getProductSnapshot().getPrice().getAmount(),
            item.getQuantity()
        );
    }
}
