package com.loopers.domain.coupon;

import com.loopers.domain.coupon.DiscountMethod.DiscountType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DiscountMethodTest {

    @Nested
    class Create {

        @Test
        void throwsException_WhenAmountIsNull() {
            assertThrows(
                IllegalArgumentException.class,
                () -> new DiscountMethod((BigDecimal) null)
            );
        }

        @Test
        void throwsException_WhenRateIsNull() {
            assertThrows(
                IllegalArgumentException.class,
                () -> new DiscountMethod((Double) null)
            );
        }

        @Test
        void create_WithFixedAmount() {
            DiscountMethod discountMethod = new DiscountMethod(BigDecimal.valueOf(1000));

            assertEquals(DiscountType.FIXED_AMOUNT, discountMethod.getDiscountType());
            assertEquals(BigDecimal.valueOf(1000), discountMethod.getAmount());
            assertNull(discountMethod.getRate());
        }

        @Test
        void create_WithRate() {
            DiscountMethod discountMethod = new DiscountMethod(0.1);

            assertEquals(DiscountType.RATE, discountMethod.getDiscountType());
            assertEquals(0.1, discountMethod.getRate());
            assertNull(discountMethod.getAmount());
        }
    }

    @Nested
    class ToPolicy {
        @Test
        void toPolicy_withFixedAmount_returnsFixedAmountPolicy() {
            // Given
            DiscountMethod discountMethod = new DiscountMethod(BigDecimal.valueOf(1000));

            // When
            DiscountPolicy policy = discountMethod.toPolicy();

            // Then
            assertThat(policy).isInstanceOf(FixedAmountPolicy.class);
        }

        @Test
        void toPolicy_withRate_returnsRatePolicy() {
            // Given
            DiscountMethod discountMethod = new DiscountMethod(0.1);

            // When
            DiscountPolicy policy = discountMethod.toPolicy();

            // Then
            assertThat(policy).isInstanceOf(RatePolicy.class);
        }

        @Test
        void toPolicy_withFixedAmountTypeAndNullAmount_throwsException() {
            // Given
            DiscountMethod discountMethod = new DiscountMethod(BigDecimal.valueOf(1000));
            ReflectionTestUtils.setField(discountMethod, "amount", null);

            // When & Then
            assertThrows(IllegalArgumentException.class, discountMethod::toPolicy);
        }

        @Test
        void toPolicy_withRateTypeAndNullRate_throwsException() {
            // Given
            DiscountMethod discountMethod = new DiscountMethod(0.5);
            ReflectionTestUtils.setField(discountMethod, "rate", null);

            // When & Then
            assertThrows(IllegalArgumentException.class, discountMethod::toPolicy);
        }

        @Test
        void toPolicy_withUnknownType_throwsException() {
            // Given
            DiscountMethod discountMethod = new DiscountMethod(BigDecimal.TEN); // 정상 객체 생성
            ReflectionTestUtils.setField(discountMethod, "discountType", null);

            // When & Then
            assertThrows(IllegalStateException.class, discountMethod::toPolicy);
        }
    }
}
