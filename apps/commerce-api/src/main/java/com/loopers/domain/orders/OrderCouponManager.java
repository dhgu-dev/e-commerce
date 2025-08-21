package com.loopers.domain.orders;

import com.loopers.domain.coupon.CouponModel;

import java.util.Optional;

public interface OrderCouponManager {
    Optional<CouponModel> find(Long id);

    void saveAndFlush(CouponModel couponModel);
}
