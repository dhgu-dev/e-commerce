package com.loopers.domain.coupon;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RatePolicyTest {

    @Nested
    class Create {

        @Test
        void throwsException_whenRateIsNegative() {
            assertThrows(
                IllegalArgumentException.class,
                () -> new RatePolicy(-0.1)
            );
        }

        @Test
        void throwsException_whenRateIsOverOne() {
            assertThrows(
                IllegalArgumentException.class,
                () -> new RatePolicy(1.1)
            );
        }

        @Test
        void create_withValidRate() {
            RatePolicy policy = new RatePolicy(0.2);

            assertEquals(0.2, policy.rate());
        }
    }

    @Nested
    class ApplyDiscount {

        @Test
        void throwsException_whenOriginalPriceIsNegative() {
            RatePolicy policy = new RatePolicy(0.2);
            BigDecimal originalPrice = BigDecimal.valueOf(-100);

            assertThrows(
                IllegalArgumentException.class,
                () -> policy.applyDiscount(originalPrice)
            );
        }

        @Test
        void applyDiscount_returnsZero_whenOriginalPriceIsZero() {
            RatePolicy policy = new RatePolicy(0.2);
            BigDecimal originalPrice = BigDecimal.ZERO;

            BigDecimal discountedPrice = policy.applyDiscount(originalPrice);

            assertEquals(BigDecimal.ZERO, discountedPrice);
        }

        @Test
        void applyDiscount_returnsCorrectValue_whenOriginalPriceIsPositive() {
            RatePolicy policy = new RatePolicy(0.2);
            BigDecimal originalPrice = BigDecimal.valueOf(1000);

            BigDecimal discountedPrice = policy.applyDiscount(originalPrice);

            assertEquals(BigDecimal.valueOf(800), discountedPrice);
        }
    }
}
