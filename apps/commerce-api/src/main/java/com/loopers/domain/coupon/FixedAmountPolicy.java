package com.loopers.domain.coupon;

import java.math.BigDecimal;

public record FixedAmountPolicy(BigDecimal amount) implements DiscountPolicy {

    public FixedAmountPolicy {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("고정 금액 할인 정책의 금액은 음수일 수 없습니다.");
        }
    }

    @Override
    public BigDecimal applyDiscount(BigDecimal originalPrice) {
        if (originalPrice.compareTo(amount) < 0) {
            return BigDecimal.ZERO;
        }
        return originalPrice.subtract(amount);
    }
}
