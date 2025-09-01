package com.loopers.domain.orders;

import com.loopers.domain.payment.PaymentModel;

public interface DataPlatformServiceOutputPort {
    void send(OrdersModel order);

    void send(PaymentModel payment);
}
