package com.loopers.domain.coupon;

import java.util.Optional;

public interface CouponRepository {
    Optional<CouponModel> find(Long id);

    void saveAndFlush(CouponModel couponModel);

    void save(CouponModel couponModel);
}
