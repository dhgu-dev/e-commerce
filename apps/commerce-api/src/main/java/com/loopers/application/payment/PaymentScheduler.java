package com.loopers.application.payment;

import com.loopers.domain.orders.OrderRepository;
import com.loopers.domain.orders.OrderResourceManager;
import com.loopers.domain.orders.OrdersModel;
import com.loopers.domain.payment.*;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentScheduler {

    private final PaymentRepository paymentRepository;
    private final PgProvider<CardPaymentRequest> pgProvider;
    private final OrderResourceManager orderResourceManager;
    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 600000) // 10분마다 실행
    public void checkPendingPayments() {
        ZonedDateTime threshold = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusMinutes(10);
        List<PaymentModel> oldPendingPayments = paymentRepository.findAllPendingOlderThan(TransactionStatus.PENDING, threshold);
        for (PaymentModel payment : oldPendingPayments) {
            var detail = pgProvider.getTransactionDetail(payment.getUserId(), payment.getTransactionKey());
            if (detail == null) {
                log.warn("No transaction detail found for payment: {}", payment.getTransactionKey());
                continue; // 상세 정보가 없는 경우, 다음 결제 확인으로 넘어감
            }

            OrdersModel order = orderRepository.find(Long.valueOf(payment.getOrderId()))
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Order not found for ID: " + payment.getOrderId()));

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
}
