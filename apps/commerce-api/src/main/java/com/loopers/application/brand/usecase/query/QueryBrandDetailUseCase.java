package com.loopers.application.brand.usecase.query;

import com.loopers.application.brand.dto.BrandInfo;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.spec.ProductSearchConditionFactory;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryBrandDetailUseCase {

    private final BrandService brandService;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public Result execute(Query query) {
        if (query == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Query cannot be null");
        }

        BrandModel brand = brandService.getDetail(query.brandId());
        List<ProductModel> products = productService.search(
            ProductSearchConditionFactory.buildBrandEqual(brand),
            PageRequest.of(0, 10)
        );

        final long displayLimit = 5L;
        List<ProductModel> rankedProducts = brandService.getRepresentativeProducts(
            brand,
            products,
            displayLimit
        );

        return new Result(BrandInfo.from(brand, rankedProducts));
    }

    public record Result(BrandInfo brandInfo) {
    }

    public record Query(Long brandId) {
    }
}
