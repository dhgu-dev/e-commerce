package com.loopers.domain.orders;

import com.loopers.domain.orders.vo.Price;
import com.loopers.domain.orders.vo.ProductSnapshot;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemModelTest {

    @DisplayName("주문 아이템을 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("주문이 null이면 예외가 발생한다.")
        @Test
        void throwsException_whenOrdersIsNull() {
            // given
            OrdersModel orders = null;
            ProductSnapshot productSnapshot = ProductSnapshot.of(1L, "productName", Price.ZERO);
            Long quantity = 1L;

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> new OrderItemModel(orders, productSnapshot, quantity));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("상품 스냅샷이 null이면 예외가 발생한다.")
        @Test
        void throwsException_whenProductSnapshotIsNull() {
            // given
            OrdersModel orders = new OrdersModel(1L, Price.ZERO);
            ProductSnapshot productSnapshot = null;
            Long quantity = 1L;

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> new OrderItemModel(orders, productSnapshot, quantity));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("수량이 null이면 예외가 발생한다.")
        @Test
        void throwsException_whenQuantityIsNull() {
            // given
            OrdersModel orders = new OrdersModel(1L, Price.ZERO);
            ProductSnapshot productSnapshot = ProductSnapshot.of(1L, "productName", Price.ZERO);
            Long quantity = null;

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> new OrderItemModel(orders, productSnapshot, quantity));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
