package com.loopers.domain.coupon;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record RatePolicy(double rate) implements DiscountPolicy {

    public RatePolicy {
        if (rate < 0 || rate > 1) {
            throw new IllegalArgumentException("할인율은 0과 1 사이의 값이어야 합니다.");
        }
    }

    @Override
    public BigDecimal applyDiscount(BigDecimal originalPrice) {
        if (originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("원래 가격은 음수일 수 없습니다.");
        }
        BigDecimal discountAmount = originalPrice.multiply(BigDecimal.valueOf(rate));
        return originalPrice.subtract(discountAmount).setScale(0, RoundingMode.CEILING);
    }
}
