package com.loopers.interfaces.event.orders;

import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.orders.DataPlatformServiceOutputPort;
import com.loopers.domain.orders.OrderEvent;
import com.loopers.domain.orders.OrderRepository;
import com.loopers.domain.orders.OrdersModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final DataPlatformServiceOutputPort dataPlatformServiceOutputPort;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCoupon(OrderEvent.OrderCreatedEvent event) {
        if (event.couponId() == null) {
            return;
        }

        CouponModel coupon = couponRepository.find(event.couponId()).orElseThrow();
        coupon.consume();
        couponRepository.saveAndFlush(coupon);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDataPlatform(OrderEvent.OrderCreatedEvent event) {
        OrdersModel order = orderRepository.find(event.orderId()).orElseThrow();
        dataPlatformServiceOutputPort.send(order);
    }

    @Async
    @EventListener
    public void handleDataPlatform(OrderEvent.OrderProcessedEvent event) {
        OrdersModel order = orderRepository.find(event.orderId()).orElseThrow();
        dataPlatformServiceOutputPort.send(order);
    }

    @Async
    @EventListener
    public void handleDataPlatform(OrderEvent.OrderCanceledEvent event) {
        OrdersModel order = orderRepository.find(event.orderId()).orElseThrow();
        dataPlatformServiceOutputPort.send(order);
    }
}
