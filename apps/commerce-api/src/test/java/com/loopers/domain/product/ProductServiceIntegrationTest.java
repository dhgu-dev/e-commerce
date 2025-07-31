package com.loopers.domain.product;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.product.spec.ProductSearchCondition;
import com.loopers.domain.product.spec.ProductSearchConditionFactory;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @Sql(statements = {
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (1, '테스트상품1', 1000, 15, 1, 10, '2023-10-01 00:00:00', '2023-10-01 00:00:00', NULL)",
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (2, '테스트상품2', 2000, 20, 1, 5, '2023-10-02 00:00:00', '2023-10-02 00:00:00', NULL)",
        "INSERT INTO product (id, name, price, stock, brand_id, like_count, created_at, updated_at, deleted_at) VALUES (3, '테스트상품3', 3000, 10, 2, 15, '2023-10-03 00:00:00', '2023-10-03 00:00:00', NULL)"
    })
    class Search {

        @Mock
        BrandModel mockBrand;

        @Test
        void throwsException_whenConditionIsNull() {
            ProductSearchCondition condition = null;
            Pageable pageable = PageRequest.of(0, 10);
            CoreException exception = assertThrows(CoreException.class, () -> productService.search(condition, pageable));

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        void throwsException_whenPageableIsNull() {
            ProductSearchCondition condition = ProductSearchConditionFactory.buildNoCondition();
            Pageable pageable = null;
            CoreException exception = assertThrows(CoreException.class, () -> productService.search(condition, pageable));

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        void returnsProducts_whenConditionAndPageableAreValid() {
            ProductSearchCondition condition = ProductSearchConditionFactory.buildNoCondition();
            Pageable pageable = PageRequest.of(0, 3);

            List<ProductModel> products = productService.search(condition, pageable);

            assertThat(products).isNotEmpty();
            assertThat(products.size()).isEqualTo(3);
            verify(productRepository).search(condition, pageable);
        }

        @Test
        void returnsProductsOfBrand_whenConditionHasBrand() {
            Long brandId = 1L;
            ProductSearchCondition condition = ProductSearchConditionFactory.buildBrandEqual(mockBrand);
            Pageable pageable = PageRequest.of(0, 3);
            when(mockBrand.getId()).thenReturn(brandId);

            List<ProductModel> products = productService.search(condition, pageable);

            assertThat(products).isNotEmpty();
            assertThat(products.size()).isEqualTo(2);
            assertThat(products.getFirst().getBrandId()).isEqualTo(brandId);
            verify(productRepository).search(condition, pageable);
        }
    }
}
