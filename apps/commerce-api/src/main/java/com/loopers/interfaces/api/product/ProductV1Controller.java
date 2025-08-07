package com.loopers.interfaces.api.product;

import com.loopers.application.product.dto.ProductInfo;
import com.loopers.application.product.usecase.query.QueryPagedProductsUseCase;
import com.loopers.application.product.usecase.query.QueryProductDetailUseCase;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductV1Controller implements ProductV1ApiSpec {

    private final QueryPagedProductsUseCase queryPagedProductsUseCase;
    private final QueryProductDetailUseCase queryProductDetailUseCase;

    @Override
    @GetMapping()
    public ApiResponse<Page<ProductV1Dto.ProductResponse>> getProducts(
        @ModelAttribute ProductV1Dto.GetProductsRequest dto
    ) {
        Sort sort = parseSort(dto.sort());
        PageRequest pageRequest = PageRequest.of(dto.page(), dto.size(), sort);

        Page<ProductInfo> products = queryPagedProductsUseCase.execute(new QueryPagedProductsUseCase.Query(dto.brandId(), pageRequest)).products();

        return ApiResponse.success(products.map(ProductV1Dto.ProductResponse::from));
    }

    private Sort parseSort(String sortString) {
        // 기본 정렬: 최신순
        Sort defaultSort = Sort.by(Sort.Direction.DESC, "latest");

        if (sortString == null || sortString.isBlank()) {
            return defaultSort;
        }

        String[] parts = sortString.split("_");
        if (parts.length != 2) {
            // 형식이 맞지 않으면 기본 정렬 반환
            return defaultSort;
        }

        String property = parts[0];
        String directionString = parts[1];

        Sort.Direction direction = "desc".equalsIgnoreCase(directionString) ?
            Sort.Direction.DESC : Sort.Direction.ASC;

        return Sort.by(direction, property);
    }

    @Override
    @GetMapping("/{productId}")
    public ApiResponse<ProductV1Dto.ProductResponse> getProduct(@PathVariable("productId") Long productId) {
        ProductInfo product = queryProductDetailUseCase.execute(new QueryProductDetailUseCase.Query(productId)).product();
        return ApiResponse.success(ProductV1Dto.ProductResponse.from(product));
    }
}
