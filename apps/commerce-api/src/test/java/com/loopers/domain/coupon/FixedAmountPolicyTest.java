package com.loopers.domain.coupon;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixedAmountPolicyTest {

    @Nested
    class Create {

        @Test
        void throwsException_whenAmountIsNegative() {
            assertThrows(
                IllegalArgumentException.class,
                () -> new FixedAmountPolicy(BigDecimal.valueOf(-100))
            );
        }

        @Test
        void create_withValidAmount() {
            FixedAmountPolicy policy = new FixedAmountPolicy(BigDecimal.valueOf(1000));

            assertEquals(BigDecimal.valueOf(1000), policy.amount());
        }
    }

    @Nested
    class ApplyDiscount {

        @Test
        void applyDiscount_returnsZero_whenOriginalPriceIsLessThanAmount() {
            FixedAmountPolicy policy = new FixedAmountPolicy(BigDecimal.valueOf(1000));
            BigDecimal originalPrice = BigDecimal.valueOf(500);

            BigDecimal discountedPrice = policy.applyDiscount(originalPrice);

            assertEquals(BigDecimal.ZERO, discountedPrice);
        }

        @Test
        void applyDiscount_returnsCorrectValue_whenOriginalPriceIsGreaterThanAmount() {
            FixedAmountPolicy policy = new FixedAmountPolicy(BigDecimal.valueOf(500));
            BigDecimal originalPrice = BigDecimal.valueOf(2000);

            BigDecimal discountedPrice = policy.applyDiscount(originalPrice);

            assertEquals(BigDecimal.valueOf(1500), discountedPrice);
        }

    }
}
