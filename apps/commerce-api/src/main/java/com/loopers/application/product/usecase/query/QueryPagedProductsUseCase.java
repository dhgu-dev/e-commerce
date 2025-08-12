package com.loopers.application.product.usecase.query;

import com.loopers.application.product.dto.ProductInfo;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.spec.ProductSearchConditionFactory;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryPagedProductsUseCase {

    private final ProductService productService;
    private final BrandService brandService;

    @Transactional(readOnly = true)
    public Result execute(Query query) {
        if (query == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Query cannot be null");
        }

        if (query.brandId == null) {
            List<ProductModel> products = productService.search(
                ProductSearchConditionFactory.buildNoCondition(),
                query.pageRequest()
            );

            long totalCount = productService.countAll(ProductSearchConditionFactory.buildNoCondition());

            Map<Long, BrandModel> brandMap = brandService.getBrands(
                    products.stream().map(ProductModel::getBrandId).collect(Collectors.toSet())
                )
                .stream()
                .collect(Collectors.toMap(BrandModel::getId, brandModel -> brandModel));

            return new Result(new PageImpl<>(
                products.stream().map(product -> ProductInfo.from(product, brandMap.get(product.getBrandId()))).toList(),
                query.pageRequest,
                totalCount
            ));
        }

        BrandModel brand = brandService.getDetail(query.brandId());

        List<ProductInfo> products = productService.search(
                ProductSearchConditionFactory.buildBrandEqual(brand),
                query.pageRequest()
            )
            .stream()
            .map(product -> ProductInfo.from(product, brand))
            .toList();

        long totalCount = productService.countAll(ProductSearchConditionFactory.buildBrandEqual(brand));

        return new Result(new PageImpl<>(products, query.pageRequest, totalCount));
    }

    public record Result(Page<ProductInfo> products) {
    }

    public record Query(Long brandId, PageRequest pageRequest) {
        public Query {
            if (pageRequest == null) {
                throw new CoreException(ErrorType.BAD_REQUEST, "Page Request cannot be null");
            }
        }
    }

}
