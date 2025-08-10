package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Optional<CouponModel> find(Long id) {
        return couponJpaRepository.findById(id);
    }

    @Override
    public void saveAndFlush(CouponModel couponModel) {
        couponJpaRepository.saveAndFlush(couponModel);
    }
}
