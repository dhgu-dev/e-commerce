package com.loopers.domain.orders.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductSnapshotTest {

    @DisplayName("상품 스냅샷을 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("상품 ID가 null 이 주어지면, 예외를 던진다.")
        @Test
        void throwsException_whenProductIdIsNull() {
            Long productId = null;
            String name = "Test Product";
            Price price = Price.of(new BigDecimal("1000"));

            CoreException exception = assertThrows(CoreException.class, () -> {
                ProductSnapshot.of(productId, name, price);
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("상품 이름이 null 혹은 빈 값 이 주어지면, 예외를 던진다.")
        @ParameterizedTest
        @NullAndEmptySource
        void throwsException_whenNameIsNull(String name) {
            Long productId = 1L;
            Price price = Price.of(new BigDecimal("1000"));

            CoreException exception = assertThrows(CoreException.class, () -> {
                ProductSnapshot.of(productId, name, price);
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("가격이 null 이 주어지면, 예외를 던진다.")
        @Test
        void throwsException_whenPriceIsNull() {
            Long productId = 1L;
            String name = "Test Product";
            Price price = null;

            CoreException exception = assertThrows(CoreException.class, () -> {
                ProductSnapshot.of(productId, name, price);
            });

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

}
