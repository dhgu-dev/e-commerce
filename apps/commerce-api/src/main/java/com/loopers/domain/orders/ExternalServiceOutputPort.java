package com.loopers.domain.orders;

public interface ExternalServiceOutputPort {
    void send(OrdersModel order);
}
