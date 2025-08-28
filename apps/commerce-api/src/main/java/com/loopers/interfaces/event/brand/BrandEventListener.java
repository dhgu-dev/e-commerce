package com.loopers.interfaces.event.brand;

import com.loopers.domain.brand.BrandEvent;
import com.loopers.domain.member.UserActionLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BrandEventListener {

    private final UserActionLogger userActionLogger;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BrandEvent.BrandProductLikedEvent event) {
        userActionLogger.logAction(event.memberId(), "LIKE_BRAND_PRODUCT", "Brand ID: " + event.brandId() + ", Product ID: " + event.productId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BrandEvent.BrandProductUnLikedEvent event) {
        userActionLogger.logAction(event.memberId(), "UNLIKE_BRAND_PRODUCT", "Brand ID: " + event.brandId() + ", Product ID: " + event.productId());
    }
}
