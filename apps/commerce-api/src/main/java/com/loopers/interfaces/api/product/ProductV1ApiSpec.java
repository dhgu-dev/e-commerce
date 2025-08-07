package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;

@Tag(name = "Product V1 API")
public interface ProductV1ApiSpec {

    @Operation(
        summary = "상품 목록 조회"
    )
    ApiResponse<Page<ProductV1Dto.ProductResponse>> getProducts(
        ProductV1Dto.GetProductsRequest dto
    );

    @Operation(
        summary = "상품 정보 조회"
    )
    ApiResponse<ProductV1Dto.ProductResponse> getProduct(
        Long productId
    );
}
