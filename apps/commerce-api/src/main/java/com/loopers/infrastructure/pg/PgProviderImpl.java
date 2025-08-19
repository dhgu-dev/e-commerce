package com.loopers.infrastructure.pg;

import com.loopers.domain.payment.CardPaymentRequest;
import com.loopers.domain.payment.PgProvider;
import com.loopers.domain.payment.TransactionResult;
import com.loopers.domain.payment.TransactionStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PgProviderImpl implements PgProvider<CardPaymentRequest> {
    @Override
    public TransactionResult requestTransaction(CardPaymentRequest request) {
        return new TransactionResult(
            UUID.randomUUID().toString().replace("-", "").substring(0, 8),
            TransactionStatus.SUCCESS,
            null
        );
    }
}
