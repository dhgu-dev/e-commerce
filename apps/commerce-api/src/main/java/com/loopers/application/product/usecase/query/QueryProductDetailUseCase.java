package com.loopers.application.product.usecase.query;

import com.loopers.application.product.dto.ProductInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryProductDetailUseCase {

    private final ProductService productService;
    private final BrandService brandService;

    public Result execute(Query query) {
        if (query == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Query cannot be null");
        }

        var product = productService.getDetail(query.productId());
        var brand = brandService.getDetail(product.getBrandId());

        return new Result(ProductInfo.from(product, brand));
    }

    public record Result(ProductInfo product) {
    }

    public record Query(Long productId) {
    }

}
