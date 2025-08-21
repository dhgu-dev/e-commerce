package com.loopers.infrastructure.pg;

import com.loopers.domain.payment.OrderResult;
import com.loopers.domain.payment.TransactionResult;

import java.util.List;

public record OrderResponse(
    String orderId,
    List<TransactionResponse> transactions
) {
    public static OrderResult toResult(OrderResponse response) {
        List<TransactionResult> transactionResults = response.transactions().stream()
            .map(TransactionResponse::toResult)
            .toList();

        return new OrderResult(
            OrderKeyEncoder.decode(response.orderId()).toString(),
            transactionResults
        );
    }
}
