package com.loopers.domain.product.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StockTest {

    @DisplayName("재고를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("음수의 수량이 주어지면, 예외가 발생한다.")
        @Test
        void throwsException_whenQuantityIsNegative() {
            CoreException result = assertThrows(CoreException.class, () -> Stock.of(-1L));
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("재고를 차감할 때, ")
    @Nested
    class Deduct {
        @DisplayName("음수의 수량이 주어지면, 예외가 발생한다.")
        @Test
        void throwsException_whenDeductingNegativeQuantity() {
            Stock stock = Stock.of(10L);
            CoreException result = assertThrows(CoreException.class, () -> stock.deduct(-1L));
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("재고보다 큰 수량을 차감하려고 하면, 예외가 발생한다.")
        @Test
        void throwsException_whenDeductingMoreThanAvailableStock() {
            Stock stock = Stock.of(5L);
            CoreException result = assertThrows(CoreException.class, () -> stock.deduct(10L));
            assertThat(result.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }
    }

    @DisplayName("재고를 비교할 때, ")
    @Nested
    class EqualsAndHashCode {
        @DisplayName("같은 수량의 재고는 동일하게 취급된다.")
        @Test
        void equalsReturnsTrue_whenStockIsEqual() {
            Stock stock1 = Stock.of(10L);
            Stock stock2 = Stock.of(10L);

            assertThat(stock1).isEqualTo(stock2);
        }

        @DisplayName("다른 수량의 재고는 동일하지 않게 취급된다.")
        @Test
        void equalsReturnsFalse_whenStockIsNotEqual() {
            Stock stock1 = Stock.of(10L);
            Stock stock2 = Stock.of(5L);

            assertThat(stock1).isNotEqualTo(stock2);
        }

        @DisplayName("동일한 재고의 해시코드는 동일하다.")
        @Test
        void hashCodeIsSame_whenStocksAreEqual() {
            Stock stock1 = Stock.of(10L);
            Stock stock2 = Stock.of(10L);

            assertThat(stock1.hashCode()).isEqualTo(stock2.hashCode());
        }
    }
}
