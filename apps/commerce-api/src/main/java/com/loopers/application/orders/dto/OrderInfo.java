package com.loopers.application.orders.dto;

import com.loopers.domain.orders.OrdersModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.math.BigDecimal;
import java.util.List;

public record OrderInfo(Long memberId, BigDecimal totalPrice, String status, List<OrderItemInfo> items) {

    public OrderInfo {
        if (memberId == null || totalPrice == null || status == null || status.isBlank() || items == null || items.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "All fields must be provided and items cannot be empty");
        }
    }

    public static OrderInfo from(OrdersModel order) {
        return new OrderInfo(
            order.getMemberId(),
            order.getTotalPrice().getAmount(),
            order.getStatus().name(),
            order.getItems().stream().map(OrderItemInfo::from).toList()
        );
    }
}
