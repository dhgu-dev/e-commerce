package com.loopers.interfaces.event.payment;

import com.loopers.domain.orders.OrderRepository;
import com.loopers.domain.orders.OrderResourceManager;
import com.loopers.domain.orders.OrdersModel;
import com.loopers.domain.payment.PaymentEvent;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderRepository orderRepository;
    private final OrderResourceManager orderResourceManager;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentEvent.PaymentCompletedEvent event) {
        OrdersModel order = orderRepository.find(Long.valueOf(event.orderId()))
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Order not found for ID: " + event.orderId()));

        order.process();
        orderRepository.save(order);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentEvent.PaymentFailedEvent event) {
        OrdersModel order = orderRepository.find(Long.valueOf(event.orderId()))
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Order not found for ID: " + event.orderId()));

        orderResourceManager.restoreResources(order);
        order.cancel();
        orderRepository.save(order);
    }

}
