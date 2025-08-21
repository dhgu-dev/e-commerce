package com.loopers.application.payment;

import com.loopers.application.payment.dto.PaymentOrder;
import com.loopers.application.payment.dto.PaymentOrderResult;
import com.loopers.application.payment.dto.SyncPaymentCommand;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberRepository;
import com.loopers.domain.orders.OrderRepository;
import com.loopers.domain.orders.OrderResourceManager;
import com.loopers.domain.orders.OrdersModel;
import com.loopers.domain.payment.*;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFacade {
    private final PaymentProcessor<CardPaymentRequest> cardPaymentProcessor;
    private final PaymentProcessor<PointPaymentRequest> pointPaymentProcessor;
    private final PgProvider<CardPaymentRequest> pgProvider;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderResourceManager orderResourceManager;

    public PaymentOrderResult requestPayment(PaymentOrder request) {
        MemberModel member = memberRepository.findByUserId(request.userId()).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
        OrdersModel order = orderRepository.find(request.orderId()).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
        switch (request.paymentMethod()) {
            case CARD -> {
                PaymentModel paymentModel = cardPaymentProcessor.pay(new CardPaymentRequest(member, order, request.amount(), request.cardType().toDomain(), request.cardNo()));
                return new PaymentOrderResult(paymentModel.getTransactionKey());
            }
            case POINTS -> {
                PaymentModel paymentModel = pointPaymentProcessor.pay(new PointPaymentRequest(member, order, request.amount()));
                if (paymentModel.getStatus() == TransactionStatus.SUCCESS) {
                    order.process();
                    orderRepository.save(order);
                }
                return new PaymentOrderResult(paymentModel.getTransactionKey());
            }
            default -> {
                log.error("Unsupported payment method: {}", request.paymentMethod());
                throw new CoreException(ErrorType.BAD_REQUEST, "Unsupported payment method: " + request.paymentMethod());
            }
        }
    }

    @Transactional
    public void syncPayment(SyncPaymentCommand command) {
        PaymentModel payment = paymentRepository.findByTxKey(command.transactionKey())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Payment not found for transaction key: " + command.transactionKey()));
        OrdersModel order = orderRepository.find(Long.valueOf(payment.getOrderId()))
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Order not found for ID: " + payment.getOrderId()));

        if (payment.getStatus() != TransactionStatus.PENDING) {
            log.warn("Payment already processed or not in pending state: {}", payment.getStatus());
            throw new CoreException(ErrorType.BAD_REQUEST, "Payment already processed or not in pending state.");
        }

        var detail = pgProvider.getTransactionDetail(payment.getUserId(), command.transactionKey());
        if (detail.status() != command.status().toDomain()) {
            log.error("Payment status mismatch: expected {}, got {}", command.status(), detail.status());
            throw new CoreException(ErrorType.BAD_REQUEST, "Payment status mismatch.");
        }

        var transactions = pgProvider.getTransactionsByOrder(payment.getUserId(), payment.getOrderId());
        if (transactions.transactions().stream().filter(t -> t.status() == TransactionStatus.SUCCESS).count() > 1) {
            log.error("Multiple successful transactions found for order: {}", command.orderId());
            throw new CoreException(ErrorType.BAD_REQUEST, "Multiple successful transactions found for order.");
        }

        switch (detail.status()) {
            case SUCCESS -> {
                payment.approve();
                paymentRepository.save(payment);
                order.process();
                orderRepository.save(order);
            }
            case FAILED -> {
                payment.failed(detail.reason());
                paymentRepository.save(payment);
                orderResourceManager.restoreResources(order);
            }
        }
    }


}
