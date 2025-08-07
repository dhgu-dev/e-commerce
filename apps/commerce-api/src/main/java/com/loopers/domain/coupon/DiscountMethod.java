package com.loopers.domain.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscountMethod {

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    @Getter
    private DiscountType discountType;

    @Getter
    private BigDecimal amount;

    @Getter
    private Double rate;

    public DiscountMethod(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("할인 금액은 null 일 수 없습니다.");
        }

        this.discountType = DiscountType.FIXED_AMOUNT;
        this.amount = amount;
    }

    public DiscountMethod(Double rate) {
        if (rate == null) {
            throw new IllegalArgumentException("할인율은 null 일 수 없습니다.");
        }

        this.discountType = DiscountType.RATE;
        this.rate = rate;
    }

    public DiscountPolicy toPolicy() {
        if (discountType == null) {
            throw new IllegalStateException("할인 유형이 설정되지 않았습니다.");
        }
        
        switch (discountType) {
            case FIXED_AMOUNT -> {
                if (amount == null) {
                    throw new IllegalArgumentException("할인 금액은 null 일 수 없습니다.");
                }
                return new FixedAmountPolicy(amount);
            }
            case RATE -> {
                if (rate == null) {
                    throw new IllegalArgumentException("할인율은 null 일 수 없습니다.");
                }
                return new RatePolicy(rate);
            }
            default -> throw new IllegalStateException("알 수 없는 할인 유형: " + discountType);
        }
    }

    public enum DiscountType {
        FIXED_AMOUNT, // 정액 할인
        RATE // 정률 할인
    }
}
