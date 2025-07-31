package com.loopers.domain.orders;

import com.loopers.domain.orders.enums.OrderStatus;
import com.loopers.domain.orders.vo.Price;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrdersModelTest {

    @Mock
    OrderItemModel mockItem;

    @DisplayName("주문 모델을 생성할 때, ")
    @Nested
    class Create {

        @DisplayName("회원 아이디가 null 이면, 예외가 발생한다.")
        @Test
        void throwsException_whenMemberIdIsNull() {
            // Given
            Long memberId = null;
            Price totalPrice = Price.of(new BigDecimal("10000"));

            // When & Then
            CoreException result = assertThrows(CoreException.class, () -> {
                new OrdersModel(memberId, totalPrice);
            });

            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("총 가격이 null 이면, 예외가 발생한다.")
        @Test
        void throwsException_whenTotalPriceIsNull() {
            // Given
            Long memberId = 1L;
            Price totalPrice = null;

            // When & Then
            CoreException result = assertThrows(CoreException.class, () -> {
                new OrdersModel(memberId, totalPrice);
            });

            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("인자가 모두 주어지면, 정상적으로 생성된다.")
        @Test
        void orderModelCreated_whenAllArgumentsAreProvided() {
            // Given
            Long memberId = 1L;
            Price totalPrice = Price.of(new BigDecimal("10000"));

            // When
            OrdersModel ordersModel = new OrdersModel(memberId, totalPrice);

            // Then
            assertAll(
                    () -> assertThat(ordersModel.getMemberId()).isEqualTo(memberId),
                    () -> assertThat(ordersModel.getTotalPrice()).isEqualTo(totalPrice),
                    () -> assertThat(ordersModel.getStatus()).isEqualTo(OrderStatus.NOT_PAID)
            );
        }
    }

    @DisplayName("주문 모델에 아이템을 추가할 때, ")
    @Nested
    class AddItem {

        @DisplayName("addItem 호출 시 아이템이 주문에 추가되고, 아이템의 orders 필드가 해당 주문을 참조한다.")
        @Test
        void setsOrderReferenceAndAddsToItems_whenAddItemIsCalled() {
            // Given
            Long memberId = 1L;
            Price totalPrice = Price.of(new BigDecimal("10000"));
            OrdersModel ordersModel = new OrdersModel(memberId, totalPrice);

            // When
            ordersModel.addItem(mockItem);

            // Then
            assertThat(ordersModel.getItems()).contains(mockItem);
            verify(mockItem).setOrders(ordersModel);
        }
    }
}
