package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardPaymentProcessor implements PaymentProcessor<CardPaymentRequest> {

    private final PaymentRepository paymentRepository;
    private final PgProvider<CardPaymentRequest> pgProvider;

    @Override
    public boolean supports(PaymentType type) {
        return type == PaymentType.CARD;
    }

    @Override
    public PaymentModel pay(CardPaymentRequest request) {
        var result = pgProvider.requestTransaction(request);
        if (result.status() != TransactionStatus.SUCCESS) {
            throw new IllegalStateException(result.reason());
        }

        PaymentModel paymentModel = new PaymentModel(
            result.transactionKey(),
            request.getMemberId(),
            request.getOrderId(),
            request.getCardType(),
            request.getCardNo(),
            request.getAmount(),
            result.status(),
            result.reason()
        );
        return paymentRepository.save(paymentModel);
    }
}
