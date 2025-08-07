package com.loopers.domain.coupon;

import com.loopers.domain.coupon.CouponModel.TargetScope;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponModelTest {

    @Nested
    class Create {

        static Stream<Arguments> invalidConstructorArgs() {
            DiscountMethod validDiscount = new DiscountMethod(0.5);
            TargetScope validScope = TargetScope.ORDER;
            return Stream.of(
                Arguments.of(validDiscount, null),
                Arguments.of(null, validScope),
                Arguments.of(null, null)
            );
        }

        static Stream<Arguments> invalidConstructorArgsWithMemberId() {
            DiscountMethod validDiscount = new DiscountMethod(0.5);
            TargetScope validScope = TargetScope.ORDER;
            Long validMemberId = 1L;
            return Stream.of(
                Arguments.of(validDiscount, validScope, null),
                Arguments.of(null, validScope, validMemberId),
                Arguments.of(validDiscount, null, validMemberId)
            );
        }

        @ParameterizedTest
        @MethodSource("invalidConstructorArgs")
        void throwsIllegalArgumentException_whenRequiredArgsAreNull(DiscountMethod discountMethod, TargetScope targetScope) {
            assertThrows(IllegalArgumentException.class, () -> {
                new CouponModel(discountMethod, targetScope);
            });
        }

        @ParameterizedTest
        @MethodSource("invalidConstructorArgsWithMemberId")
        void throwsIllegalArgumentException_whenRequiredArgsWithMemberIdAreNull(DiscountMethod discountMethod, TargetScope targetScope, Long memberId) {
            assertThrows(IllegalArgumentException.class, () -> {
                new CouponModel(discountMethod, targetScope, memberId);
            });
        }

        @Test
        void createCouponModel_whenValidArgsAreProvided() {
            DiscountMethod discountMethod = new DiscountMethod(0.5);
            TargetScope targetScope = TargetScope.ORDER;

            CouponModel couponModel = new CouponModel(discountMethod, targetScope);

            assertThat(couponModel.getCode()).isNotNull();
            assertThat(couponModel.getDiscountMethod()).isEqualTo(discountMethod);
            assertThat(couponModel.getTargetScope()).isEqualTo(targetScope);
        }

        @Test
        void createCouponModel_whenValidArgsWithMemberIdAreProvided() {
            DiscountMethod discountMethod = new DiscountMethod(0.5);
            TargetScope targetScope = TargetScope.ORDER;
            Long memberId = 1L;

            CouponModel couponModel = new CouponModel(discountMethod, targetScope, memberId);

            assertThat(couponModel.getCode()).isNotNull();
            assertThat(couponModel.getDiscountMethod()).isEqualTo(discountMethod);
            assertThat(couponModel.getTargetScope()).isEqualTo(targetScope);
            assertThat(couponModel.getMemberId()).isEqualTo(memberId);
            assertThat(couponModel.getIssuedAt()).isNotNull();
        }
    }

    @Nested
    class IssueTo {

        @Test
        void issueTo_whenAlreadyIssued_throwsIllegalStateException() {
            DiscountMethod discountMethod = new DiscountMethod(0.5);
            TargetScope targetScope = TargetScope.ORDER;
            Long memberId = 1L;
            CouponModel couponModel = new CouponModel(discountMethod, targetScope, memberId);

            assertThrows(IllegalStateException.class, () -> {
                couponModel.issueTo(memberId);
            });
        }

        @Test
        void issueTo_whenNotIssued_setsMemberIdAndIssuedAt() {
            DiscountMethod discountMethod = new DiscountMethod(0.5);
            TargetScope targetScope = TargetScope.ORDER;
            Long memberId = 1L;

            CouponModel couponModel = new CouponModel(discountMethod, targetScope);

            couponModel.issueTo(memberId);

            assertThat(couponModel.getMemberId()).isEqualTo(memberId);
            assertThat(couponModel.getIssuedAt()).isNotNull();
        }
    }

    @Nested
    class Apply {
        @Test
        void apply_whenDiscountMethodIsNull_throwsIllegalStateException() throws Exception {
            CouponModel couponModel = new CouponModel(new DiscountMethod(0.5), TargetScope.ORDER);

            ReflectionTestUtils.setField(couponModel, "discountMethod", null);

            assertThrows(IllegalStateException.class, () -> {
                couponModel.apply(BigDecimal.valueOf(100));
            });
        }

        @Test
        void apply_whenValidDiscountMethod_appliesDiscount() {
            DiscountMethod discountMethod = new DiscountMethod(BigDecimal.valueOf(20));
            CouponModel couponModel = new CouponModel(discountMethod, TargetScope.ORDER);

            BigDecimal originalPrice = BigDecimal.valueOf(100);
            BigDecimal discountedPrice = couponModel.apply(originalPrice);

            assertThat(discountedPrice).isEqualTo(BigDecimal.valueOf(80));
        }

        @Test
        void apply_whenDeletedAtIsNotNull_throwsIllegalStateException() {
            DiscountMethod discountMethod = new DiscountMethod(0.5);
            CouponModel couponModel = new CouponModel(discountMethod, TargetScope.ORDER);
            ReflectionTestUtils.setField(couponModel, "deletedAt", ZonedDateTime.now());

            assertThrows(IllegalStateException.class, () -> {
                couponModel.apply(BigDecimal.valueOf(100));
            });
        }

        @Test
        void apply_whenApplied_deletesCoupon() {
            DiscountMethod discountMethod = new DiscountMethod(0.5);
            CouponModel couponModel = new CouponModel(discountMethod, TargetScope.ORDER);

            BigDecimal originalPrice = BigDecimal.valueOf(100);
            couponModel.apply(originalPrice);

            assertThat(couponModel.getDeletedAt()).isNotNull();
        }
    }

    @Nested
    class HasOwned {

        @Test
        void hasOwned_whenGivenMemberIdIsNull_returnsFalse() {
            CouponModel couponModel = new CouponModel(new DiscountMethod(0.5), TargetScope.ORDER);
            assertThat(couponModel.hasOwned(null)).isFalse();
        }

        @Test
        void hasOwned_whenCouponMemberIdIsNull_returnsFalse() {
            CouponModel couponModel = new CouponModel(new DiscountMethod(0.5), TargetScope.ORDER);
            assertThat(couponModel.hasOwned(1L)).isFalse();
        }

        @Test
        void hasOwned_whenMemberIdMatches_returnsTrue() {
            Long memberId = 1L;
            CouponModel couponModel = new CouponModel(new DiscountMethod(0.5), TargetScope.ORDER, memberId);
            assertThat(couponModel.hasOwned(memberId)).isTrue();
        }

        @Test
        void hasOwned_whenMemberIdDoesNotMatch_returnsFalse() {
            Long memberId = 1L;
            CouponModel couponModel = new CouponModel(new DiscountMethod(0.5), TargetScope.ORDER, memberId);
            assertThat(couponModel.hasOwned(2L)).isFalse();
        }
    }
}
