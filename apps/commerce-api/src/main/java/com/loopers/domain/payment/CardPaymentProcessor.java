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
        System.out.println("CardPaymentProcessor.pay() - result: " + result);

        PaymentModel paymentModel = PaymentModel.createCardPayment(
            result.transactionKey(),
            request.getMember().getUserId(),
            request.getOrder().getId().toString(),
            request.getCardType(),
            request.getCardNo(),
            request.getAmount()
        );
        return paymentRepository.save(paymentModel);
    }
}
