package com.loopers.domain.payment;

import java.math.BigDecimal;

public record TransactionDetailResult(
    String transactionKey,
    String orderId,
    CardType cardType,
    String cardNo,
    BigDecimal amount,
    TransactionStatus status,
    String reason
) {
}
