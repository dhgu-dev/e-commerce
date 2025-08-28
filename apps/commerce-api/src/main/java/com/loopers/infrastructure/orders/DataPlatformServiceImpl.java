package com.loopers.infrastructure.orders;

import com.loopers.domain.orders.DataPlatformServiceOutputPort;
import com.loopers.domain.orders.OrdersModel;
import com.loopers.domain.payment.PaymentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DataPlatformServiceImpl implements DataPlatformServiceOutputPort {

    private static final Logger log = LoggerFactory.getLogger(DataPlatformServiceImpl.class);

    @Override
    public void send(OrdersModel order) {
        log.info("Sending order info to data platform: {}", order);
    }

    @Override
    public void send(PaymentModel payment) {
        log.info("Sending payment info to data platform: {}", payment);
    }

}
