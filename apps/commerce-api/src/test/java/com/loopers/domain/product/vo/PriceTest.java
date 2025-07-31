package com.loopers.domain.product.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PriceTest {

    @DisplayName("가격을 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("null 이나 음수가 주어진 경우, 예외가 발생한다.")
        @Test
        void throwsException_whenAmountIsNullOrNegative() {
            assertAll(
                () -> {
                    CoreException result = assertThrows(
                        CoreException.class,
                        () -> Price.of(null)
                    );
                    assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                },
                () -> {
                    CoreException result = assertThrows(
                        CoreException.class,
                        () -> Price.of(new BigDecimal("-1"))
                    );
                    assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                }
            );
        }
    }

    @DisplayName("가격을 더할 때, ")
    @Nested
    class Add {
        @DisplayName("null 이 주어진 경우, 예외가 발생한다.")
        @Test
        void throwsException_whenAddingNullOrNegativePrice() {
            Price price = Price.of(new BigDecimal("100"));

            CoreException result = assertThrows(
                CoreException.class,
                () -> price.add(null)
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("올바른 값이 주어지면, 정상적으로 가격을 더할 수 있다.")
        @Test
        void canAddPrices() {
            Price price1 = Price.of(new BigDecimal("100"));
            Price price2 = Price.of(new BigDecimal("50"));
            Price result = price1.add(price2);

            assertThat(result).isEqualTo(Price.of(new BigDecimal("150")));
        }
    }

    @DisplayName("가격을 뺄 때, ")
    @Nested
    class Subtract {
        @DisplayName("null 이 주어진 경우, 예외가 발생한다.")
        @Test
        void throwsException_whenSubtractingNull() {
            Price price = Price.of(new BigDecimal("100"));

            CoreException result = assertThrows(
                CoreException.class,
                () -> price.subtract(null)
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("감소할 가격이 현재 가격보다 크면, 예외가 발생한다.")
        @Test
        void throwsException_whenSubtractingMoreThanCurrentPrice() {
            Price price1 = Price.of(new BigDecimal("100"));
            Price price2 = Price.of(new BigDecimal("150"));

            CoreException result = assertThrows(
                CoreException.class,
                () -> price1.subtract(price2)
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("올바른 값이 주어지면, 정상적으로 가격을 뺄 수 있다.")
        @Test
        void canSubtractPrices() {
            Price price1 = Price.of(new BigDecimal("100"));
            Price price2 = Price.of(new BigDecimal("50"));
            Price result = price1.subtract(price2);

            assertThat(result).isEqualTo(Price.of(new BigDecimal("50")));
        }
    }

    @DisplayName("가격 비교를 할 때, ")
    @Nested
    class EqualsAndHashCode {
        @DisplayName("같은 가격이면, 동일하게 취급된다.")
        @Test
        void equalsReturnsTrue_whenPriceIsEqual() {
            Price price1 = Price.of(new BigDecimal("100"));
            Price price2 = Price.of(new BigDecimal("100"));

            assertThat(price1).isEqualTo(price2);
        }

        @DisplayName("다른 가격이면, equals 가 false 를 반환한다.")
        @Test
        void equalsReturnsFalse_whenPricesAreNotEqual() {
            Price price1 = Price.of(new BigDecimal("100"));
            Price price2 = Price.of(new BigDecimal("50"));

            assertThat(price1).isNotEqualTo(price2);
        }

        @DisplayName("동일한 가격의 해시코드는 동일하다.")
        @Test
        void hashCodeIsSame_whenPricesAreEqual() {
            Price price1 = Price.of(new BigDecimal("100"));
            Price price2 = Price.of(new BigDecimal("100"));

            assertThat(price1.hashCode()).isEqualTo(price2.hashCode());
        }
    }

    @DisplayName("가격을 곱할 때, ")
    @Nested
    class Multiply {
        @DisplayName("음수를 곱하면 예외가 발생한다.")
        @Test
        void throwsException_whenMultiplierIsNegative() {
            Price price = Price.of(new BigDecimal("100"));
            CoreException ex = assertThrows(
                CoreException.class,
                () -> price.multiply(-1)
            );
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("0 또는 양수를 곱하면 정상적으로 곱해진다.")
        @Test
        void canMultiplyPrice() {
            Price price = Price.of(new BigDecimal("100"));
            assertThat(price.multiply(0)).isEqualTo(Price.of(BigDecimal.ZERO));
            assertThat(price.multiply(3)).isEqualTo(Price.of(new BigDecimal("300")));
        }
    }
}
