package com.loopers.domain.orders;

import com.loopers.domain.coupon.CouponModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderResourceManager {

    private final OrderCouponManager orderCouponManager;
    private final ProductStockManager productStockManager;

    @Transactional
    public void restoreResources(OrdersModel order) {
        if (order.getCouponId() != null) {
            CouponModel couponModel = orderCouponManager.find(order.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found for ID: " + order.getCouponId()));
            couponModel.restore();
            orderCouponManager.saveAndFlush(couponModel);
        }

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            productStockManager.restoreAllStock(order.getItems());
        }
    }
}
