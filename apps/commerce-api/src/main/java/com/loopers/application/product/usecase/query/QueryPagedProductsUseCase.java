package com.loopers.application.product.usecase.query;

import com.fasterxml.jackson.core.type.TypeReference;
import com.loopers.application.common.dto.PageResult;
import com.loopers.application.product.dto.ProductInfo;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.common.CacheManager;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.spec.ProductSearchConditionFactory;
import com.loopers.support.cache.CacheKeyFactory;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryPagedProductsUseCase {

    private final ProductService productService;
    private final BrandService brandService;
    private final CacheManager<PageResult<ProductInfo>> cacheManager;

    @Transactional(readOnly = true)
    public Result execute(Query query) {
        if (query == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Query cannot be null");
        }

        var cacheKey = CacheKeyFactory.createKeyForPagedQuery(
            "products",
            query.brandId() != null ? Map.of("brandId", query.brandId()) : null,
            query.pageRequest.getSort(),
            query.pageRequest.getPageNumber(),
            query.pageRequest.getPageSize()
        );

        try {
            var cached = cacheManager.find(cacheKey, new TypeReference<>() {
            });
            if (cached.isPresent()) {
                return new Result(cached.get());
            }
        } catch (Exception e) {
            log.error("Cache retrieval failed: {}", e.getMessage(), e);
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

            var pagingResult = new PageResult<>(
                products.stream().map(product -> ProductInfo.from(product, brandMap.get(product.getBrandId()))).toList(),
                query.pageRequest,
                totalCount
            );

            try {
                cacheManager.save(cacheKey, pagingResult, new TypeReference<>() {
                }, 60, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("Failed to cache paged products for key: {}", cacheKey, e);
            }

            return new Result(pagingResult);
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

        var pagingResult = new PageResult<>(products, query.pageRequest, totalCount);

        try {
            cacheManager.save(cacheKey, pagingResult, new TypeReference<>() {
            }, 60, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to cache paged products for key: {}", cacheKey, e);
        }

        return new Result(pagingResult);
    }

    public record Result(PageResult<ProductInfo> products) {
    }

    public record Query(Long brandId, PageRequest pageRequest) {
        public Query {
            if (pageRequest == null) {
                throw new CoreException(ErrorType.BAD_REQUEST, "Page Request cannot be null");
            }
        }
    }

}
