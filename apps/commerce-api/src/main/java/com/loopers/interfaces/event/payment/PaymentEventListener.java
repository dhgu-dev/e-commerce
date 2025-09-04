package com.loopers.interfaces.event.payment;

import com.loopers.domain.orders.*;
import com.loopers.domain.payment.PaymentEvent;
import com.loopers.domain.payment.PaymentModel;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.domain.product.ProductEvent;
import com.loopers.domain.product.ProductEventPublisher;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderRepository orderRepository;
    private final OrderResourceManager orderResourceManager;
    private final OrderEventPublisher orderEventPublisher;
    private final DataPlatformServiceOutputPort dataPlatformServiceOutputPort;
    private final PaymentRepository paymentRepository;
    private final ProductEventPublisher productEventPublisher;
    private final ProductRepository productRepository;


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
        for (var item : order.getItems()) {
            orderEventPublisher.pusblish(new OrderEvent.ProductSoldEvent(
                UUID.randomUUID().toString(),
                order.getId(),
                item.getProductSnapshot().getProductId(),
                item.getQuantity(),
                ZonedDateTime.now(),
                "ProductSoldEvent",
                order.getMemberId()
            ));
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
        for (var item : order.getItems()) {
            ProductModel product = productRepository.findWithLock(item.getProductSnapshot().getProductId()).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "Product not found with ID: " + item.getProductSnapshot().getProductId())
            );
            productEventPublisher.publish(new ProductEvent.StockAdjustedEvent(
                UUID.randomUUID().toString(),
                item.getProductSnapshot().getProductId(),
                product.getStock().getQuantity(),
                ZonedDateTime.now(),
                "StockAdjustedEvent",
                order.getMemberId()
            ));
        }
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
