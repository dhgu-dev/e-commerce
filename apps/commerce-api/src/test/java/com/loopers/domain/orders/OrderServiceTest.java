package com.loopers.domain.orders;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.enums.Gender;
import com.loopers.domain.orders.vo.Price;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.vo.Stock;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderService orderService;

    @Nested
    @DisplayName("order 메서드")
    class Order {

        @Test
        @DisplayName("정상 주문 시 주문과 아이템 저장이 호출된다")
        void order_success() {
            MemberModel member = new MemberModel("user1", Gender.FEMALE, "2000-01-01", "test@test.com", 1000L);
            ProductModel product = mock(ProductModel.class);
            when(product.getId()).thenReturn(10L);
            when(product.getName()).thenReturn("상품");
            when(product.getPrice()).thenReturn(com.loopers.domain.product.vo.Price.of(new BigDecimal("1000")));

            List<Pair<ProductModel, Long>> products = List.of(Pair.of(product, 2L));
            OrdersModel order = new OrdersModel(member.getId(), Price.of(new BigDecimal("2000")));

            when(orderRepository.save(any(OrdersModel.class))).thenReturn(order);

            OrdersModel result = orderService.order(member, products, null);

            assertThat(result).isNotNull();
            verify(orderRepository).save(any(OrdersModel.class));
            verify(orderRepository).saveAll(any());
        }

        @Test
        @DisplayName("member가 null이면 BAD_REQUEST 예외 발생")
        void order_memberNull_throwsException() {
            List<Pair<ProductModel, Long>> products = List.of();
            CoreException ex = assertThrows(CoreException.class, () -> orderService.order(null, products, null));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("products가 null이면 BAD_REQUEST 예외 발생")
        void order_productsNull_throwsException() {
            MemberModel member = new MemberModel("user1", Gender.FEMALE, "2000-01-01", "test@test.com", 1000L);
            CoreException ex = assertThrows(CoreException.class, () -> orderService.order(member, null, null));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("products가 비어있으면 BAD_REQUEST 예외 발생")
        void order_productsEmpty_throwsException() {
            MemberModel member = new MemberModel("user1", Gender.FEMALE, "2000-01-01", "test@test.com", 1000L);
            CoreException ex = assertThrows(CoreException.class, () -> orderService.order(member, Collections.emptyList(), null));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("quantity가 0 이하이면 BAD_REQUEST 예외 발생")
        void order_quantityZeroOrNegative_throwsException() {
            MemberModel member = new MemberModel("user1", Gender.FEMALE, "2000-01-01", "test@test.com", 1000L);
            ReflectionTestUtils.setField(member, "id", 123L);
            ProductModel product = new ProductModel("p1", com.loopers.domain.product.vo.Price.ZERO, Stock.of(1568), 369L);
            ReflectionTestUtils.setField(product, "id", 123L);
            List<Pair<ProductModel, Long>> products = List.of(Pair.of(product, 0L));
            CoreException ex = assertThrows(CoreException.class, () -> orderService.order(member, products, null));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("getOrders 메서드")
    class GetOrders {
        @Test
        @DisplayName("정상적으로 주문 목록을 조회한다")
        void getOrders_success() {
            MemberModel member = new MemberModel("user1", Gender.FEMALE, "2000-01-01", "test@test.com", 1000L);
            List<OrdersModel> orders = List.of(mock(OrdersModel.class));
            when(orderRepository.search(member.getId())).thenReturn(orders);

            List<OrdersModel> result = orderService.getOrders(member);

            assertThat(result).isEqualTo(orders);
        }

        @Test
        @DisplayName("member가 null이면 BAD_REQUEST 예외 발생")
        void getOrders_memberNull_throwsException() {
            CoreException ex = assertThrows(CoreException.class, () -> orderService.getOrders(null));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("getDetail 메서드")
    class GetDetail {
        @Test
        @DisplayName("정상적으로 주문 상세를 조회한다")
        void getDetail_success() {
            OrdersModel order = mock(OrdersModel.class);
            when(orderRepository.find(1L)).thenReturn(Optional.of(order));

            OrdersModel result = orderService.getDetail(1L);

            assertThat(result).isEqualTo(order);
        }

        @Test
        @DisplayName("orderId가 null이면 BAD_REQUEST 예외 발생")
        void getDetail_orderIdNull_throwsException() {
            CoreException ex = assertThrows(CoreException.class, () -> orderService.getDetail(null));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("존재하지 않는 주문이면 NOT_FOUND 예외 발생")
        void getDetail_notFound_throwsException() {
            when(orderRepository.find(1L)).thenReturn(Optional.empty());
            CoreException ex = assertThrows(CoreException.class, () -> orderService.getDetail(1L));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
