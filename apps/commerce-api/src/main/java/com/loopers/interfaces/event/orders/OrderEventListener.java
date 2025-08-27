package com.loopers.interfaces.event.orders;

import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.orders.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final CouponRepository couponRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderEvent.OrderCreatedEvent event) {
        if (event.couponId() == null) {
            return;
        }

        CouponModel coupon = couponRepository.find(event.couponId()).orElseThrow();
        coupon.consume();
        couponRepository.saveAndFlush(coupon);
    }
}
