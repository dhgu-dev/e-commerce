package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.dto.SyncPaymentCommand;

public class PaymentV1Dto {
    public record PaymentCallbackRequest(
        String transactionKey,
        String orderId,
        SyncPaymentCommand.CardTypeInfo cardType,
        String cardNo,
        Long amount,
        SyncPaymentCommand.TransactionStatusInfo status,
        String reason
    ) {
    }
}
