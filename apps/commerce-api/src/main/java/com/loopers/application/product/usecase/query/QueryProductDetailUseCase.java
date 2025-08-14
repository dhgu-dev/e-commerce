package com.loopers.application.product.usecase.query;

import com.loopers.application.product.dto.ProductInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.common.CacheManager;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryProductDetailUseCase {

    private final ProductService productService;
    private final BrandService brandService;
    private final CacheManager<ProductInfo> cacheManager;

    @Transactional(readOnly = true)
    public Result execute(Query query) {
        if (query == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Query cannot be null");
        }

        try {
            Optional<ProductInfo> cached = cacheManager.find("product:detail:" + query.productId(), ProductInfo.class);
            if (cached.isPresent()) {
                return new Result(cached.get());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        var product = productService.getDetail(query.productId());
        var brand = brandService.getDetail(product.getBrandId());

        var result = ProductInfo.from(product, brand);

        try {
            cacheManager.save("product:detail:" + query.productId(), result, ProductInfo.class, 60, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to cache product detail for productId: {}", query.productId(), e);
        }

        return new Result(result);
    }

    public record Result(ProductInfo product) {
    }

    public record Query(Long productId) {
    }

}
