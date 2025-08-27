package com.loopers.interfaces.event.payment;

import com.loopers.domain.orders.*;
import com.loopers.domain.payment.PaymentEvent;
import com.loopers.domain.payment.PaymentModel;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderRepository orderRepository;
    private final OrderResourceManager orderResourceManager;
    private final OrderEventPublisher orderEventPublisher;
    private final DataPlatformServiceOutputPort dataPlatformServiceOutputPort;
    private final PaymentRepository paymentRepository;


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrder(PaymentEvent.PaymentCompletedEvent event) {
        OrdersModel order = orderRepository.find(Long.valueOf(event.orderId()))
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Order not found for ID: " + event.orderId()));

        order.process();
        orderRepository.save(order);
        orderEventPublisher.publish(
            new OrderEvent.OrderProcessedEvent(
                order.getId(),
                order.getMemberId(),
                order.getItems().stream().map(item -> item.getProductSnapshot().getProductId()).toList(),
                order.getCouponId()
            )
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrder(PaymentEvent.PaymentFailedEvent event) {
        OrdersModel order = orderRepository.find(Long.valueOf(event.orderId()))
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Order not found for ID: " + event.orderId()));

        orderResourceManager.restoreResources(order);
        order.cancel();
        orderRepository.save(order);
        orderEventPublisher.publish(
            new OrderEvent.OrderCanceledEvent(
                order.getId(),
                order.getMemberId(),
                order.getItems().stream().map(item -> item.getProductSnapshot().getProductId()).toList(),
                order.getCouponId()
            )
        );
    }

    @Async
    @EventListener
    public void handleDataPlatform(PaymentEvent.PaymentCompletedEvent event) {
        PaymentModel payment = paymentRepository.findByTxKey(event.transactionKey()).orElseThrow();
        dataPlatformServiceOutputPort.send(payment);
    }

    @Async
    @EventListener
    public void handleDataPlatform(PaymentEvent.PaymentFailedEvent event) {
        PaymentModel payment = paymentRepository.findByTxKey(event.transactionKey()).orElseThrow();
        dataPlatformServiceOutputPort.send(payment);
    }

}
