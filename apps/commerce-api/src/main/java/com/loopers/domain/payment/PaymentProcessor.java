package com.loopers.domain.payment;

public interface PaymentProcessor<T extends PaymentRequest> {
    boolean supports(PaymentType type);

    PaymentModel pay(T request);
}
