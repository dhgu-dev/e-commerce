package com.loopers.domain.product;

import com.loopers.domain.product.spec.ProductSearchCondition;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Nested
    @DisplayName("search")
    class Search {
        @Test
        void 정상_조회() {
            ProductSearchCondition condition = mock(ProductSearchCondition.class);
            Pageable pageable = PageRequest.of(0, 10);
            List<ProductModel> products = List.of(mock(ProductModel.class));
            when(productRepository.search(condition, pageable)).thenReturn(products);

            List<ProductModel> result = productService.search(condition, pageable);

            assertThat(result).isEqualTo(products);
        }

        @Test
        void condition_null_예외() {
            Pageable pageable = PageRequest.of(0, 10);
            assertThrows(CoreException.class, () -> productService.search(null, pageable));
        }

        @Test
        void pageable_null_예외() {
            ProductSearchCondition condition = mock(ProductSearchCondition.class);
            assertThrows(CoreException.class, () -> productService.search(condition, null));
        }
    }

    @Nested
    @DisplayName("countAll")
    class CountAll {
        @Test
        void 정상_조회() {
            ProductSearchCondition condition = mock(ProductSearchCondition.class);
            when(productRepository.getTotalAmount(condition)).thenReturn(5L);

            long count = productService.countAll(condition);

            assertThat(count).isEqualTo(5L);
        }

        @Test
        void condition_null_예외() {
            assertThrows(CoreException.class, () -> productService.countAll(null));
        }
    }

    @Nested
    @DisplayName("getProducts")
    class GetProducts {
        @Test
        void 정상_조회() {
            Set<Long> ids = Set.of(1L, 2L);
            List<ProductModel> products = List.of(mock(ProductModel.class));
            when(productRepository.findAll(ids)).thenReturn(products);

            List<ProductModel> result = productService.getProducts(ids);

            assertThat(result).isEqualTo(products);
        }

        @Test
        void ids_null_예외() {
            assertThrows(CoreException.class, () -> productService.getProducts(null));
        }

        @Test
        void ids_empty_빈리스트() {
            List<ProductModel> result = productService.getProducts(Collections.emptySet());
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getDetail")
    class GetDetail {
        @Test
        void 정상_조회() {
            ProductModel product = mock(ProductModel.class);
            when(productRepository.find(1L)).thenReturn(Optional.of(product));

            ProductModel result = productService.getDetail(1L);

            assertThat(result).isEqualTo(product);
        }

        @Test
        void id_null_예외() {
            assertThrows(CoreException.class, () -> productService.getDetail(null));
        }

        @Test
        void 존재하지않는_상품_예외() {
            when(productRepository.find(1L)).thenReturn(Optional.empty());
            CoreException ex = assertThrows(CoreException.class, () -> productService.getDetail(1L));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("updateProductLikeCount")
    class UpdateProductLikeCount {
        @Test
        void 정상_업데이트() {
            ProductModel product = mock(ProductModel.class);
            when(productRepository.save(product)).thenReturn(product);

            ProductModel result = productService.updateProductLikeCount(product, 10L);

            verify(product).updateLikeCount(10L);
            verify(productRepository).save(product);
            assertThat(result).isEqualTo(product);
        }

        @Test
        void product_null_예외() {
            assertThrows(CoreException.class, () -> productService.updateProductLikeCount(null, 1L));
        }

        @Test
        void likeCount_null_예외() {
            ProductModel product = mock(ProductModel.class);
            assertThrows(CoreException.class, () -> productService.updateProductLikeCount(product, null));
        }
    }

    @Nested
    @DisplayName("decreaseStock")
    class DecreaseStock {
        @Test
        void 정상_차감() {
            ProductModel product = mock(ProductModel.class);
            Stock stock = Stock.of(10L);
            when(product.getStock()).thenReturn(stock);

            productService.decreaseStock(product, 5L);

            verify(product).decreaseStock(5L);
            verify(productRepository).save(product);
        }

        @Test
        void product_null_예외() {
            assertThrows(CoreException.class, () -> productService.decreaseStock(null, 1L));
        }

        @Test
        void quantity_0이하_예외() {
            ProductModel product = mock(ProductModel.class);
            assertThrows(CoreException.class, () -> productService.decreaseStock(product, 0L));
        }

        @Test
        void 재고부족_예외() {
            ProductModel product = mock(ProductModel.class);
            when(product.getStock()).thenReturn(mock(Stock.class));
            when(product.getStock().getQuantity()).thenReturn(2L);
            when(product.getId()).thenReturn(1L);

            CoreException ex = assertThrows(CoreException.class, () -> productService.decreaseStock(product, 5L));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }
    }
}
