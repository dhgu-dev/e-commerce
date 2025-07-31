package com.loopers.domain.product;

import com.loopers.domain.product.vo.Price;
import com.loopers.domain.product.vo.Stock;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductModelTest {

    @Mock
    Stock mockStock;

    @DisplayName("상품 모델을 생성할 때, ")
    @Nested
    class Create {

        @DisplayName("이름이 null 이거나 빈칸이면, BAD_REQUEST 예외가 발생한다")
        @ParameterizedTest
        @NullAndEmptySource
        void throwsBadRequestException_whenNameIsNullOrBlank(String name) {
            // When & Then
            CoreException result = assertThrows(
                CoreException.class,
                () -> new ProductModel(name, Price.ZERO, Stock.ZERO, 1L)
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("가격이 null 이면, BAD_REQUEST 예외가 발생한다")
        @Test
        void throwsBadRequestException_whenPriceIsNull() {
            // When & Then
            CoreException result = assertThrows(
                CoreException.class,
                () -> new ProductModel("product name", null, Stock.ZERO, 1L)
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("재고가 null 이면, BAD_REQUEST 예외가 발생한다")
        @Test
        void throwsBadRequestException_whenStockIsNull() {
            // When & Then
            CoreException result = assertThrows(
                CoreException.class,
                () -> new ProductModel("product name", Price.ZERO, null, 1L)
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("브랜드 아이디가 null 이면, BAD_REQUEST 예외가 발생한다")
        @Test
        void throwsBadRequestException_whenBrandIdIsNull() {
            // When & Then
            CoreException result = assertThrows(
                CoreException.class,
                () -> new ProductModel("product name", Price.ZERO, Stock.ZERO, null)
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("모든 필드가 유효하면, 정상적으로 생성된다.")
        @Test
        void createProductModel_whenAllFieldsAreValid() {
            // Given
            String name = "Test Product";
            Price price = Price.of(BigDecimal.valueOf(1000));
            Stock stock = Stock.of(10L);
            Long brandId = 1L;

            // When
            ProductModel productModel = new ProductModel(name, price, stock, brandId);

            // Then
            assertThat(productModel.getName()).isEqualTo(name);
            assertThat(productModel.getPrice()).isEqualTo(price);
            assertThat(productModel.getStock()).isEqualTo(stock);
            assertThat(productModel.getBrandId()).isEqualTo(brandId);
            assertThat(productModel.getLikeCount()).isEqualTo(0);
        }
    }

    @DisplayName("상품 모델의 재고를 감소시킬 때, ")
    @Nested
    class DecreaseStock {

        @DisplayName("상품 모델의 재고 감소 시, Stock.deduct()가 호출된다")
        @Test
        void decreaseStock_callsDeductMethodOfStock() {
            // Given
            when(mockStock.deduct(5L)).thenReturn(mockStock);
            ProductModel productModel = new ProductModel("상품명", Price.ZERO, mockStock, 1L);

            // When
            productModel.decreaseStock(5L);

            // Then
            verify(mockStock).deduct(5L);
            assertThat(productModel.getStock()).isEqualTo(mockStock);
        }
    }

    @DisplayName("좋아요 수를 업데이트할 때, ")
    @Nested
    class UpdateLikeCount {

        @DisplayName("0 이상의 값이면 정상적으로 업데이트된다")
        @Test
        void updateLikeCount_success() {
            ProductModel product = new ProductModel("상품", Price.ZERO, Stock.ZERO, 1L);

            product.updateLikeCount(10L);

            assertThat(product.getLikeCount()).isEqualTo(10L);

            product.updateLikeCount(0L);
            assertThat(product.getLikeCount()).isEqualTo(0L);
        }

        @DisplayName("음수로 업데이트하면 BAD_REQUEST 예외가 발생한다")
        @Test
        void updateLikeCount_negative_throwsException() {
            ProductModel product = new ProductModel("상품", Price.ZERO, Stock.ZERO, 1L);

            CoreException ex = assertThrows(CoreException.class, () -> product.updateLikeCount(-1L));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
