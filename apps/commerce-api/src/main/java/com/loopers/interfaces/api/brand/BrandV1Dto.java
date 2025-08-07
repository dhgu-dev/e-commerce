package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.dto.BrandInfo;
import com.loopers.application.product.dto.ProductInfo;

import java.math.BigDecimal;
import java.util.List;

public class BrandV1Dto {
    public record BrandResponse(Long id, String name, String description, List<ProductResponse> products) {
        public static BrandResponse from(BrandInfo brand) {
            return new BrandResponse(brand.id(), brand.name(), brand.description(), brand.products().stream().map(ProductResponse::from).toList());
        }

        public record ProductResponse(Long id, String name, BigDecimal price, long likeCount) {
            public static ProductResponse from(ProductInfo product) {
                return new ProductResponse(product.id(), product.name(), product.price(), product.likeCount());
            }
        }
    }
}
