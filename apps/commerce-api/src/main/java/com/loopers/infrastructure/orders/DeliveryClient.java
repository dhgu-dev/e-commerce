package com.loopers.infrastructure.orders;

import com.loopers.domain.orders.ExternalServiceOutputPort;
import com.loopers.domain.orders.OrdersModel;
import org.springframework.stereotype.Component;

@Component
public class DeliveryClient implements ExternalServiceOutputPort {

    @Override
    public void send(OrdersModel order) {

    }

}
