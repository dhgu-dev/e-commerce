package com.loopers.domain.payment;


import java.util.List;

public record OrderResult(
    String orderId,
    List<TransactionResult> transactions
) {
}
