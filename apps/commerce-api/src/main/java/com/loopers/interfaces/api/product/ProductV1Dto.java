package com.loopers.interfaces.api.product;

import com.loopers.application.product.dto.ProductInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class ProductV1Dto {
    public record ProductResponse(
        Long id, String name, BigDecimal price, Long stock, long likeCount, String brandName
    ) {
        public static ProductResponse from(ProductInfo product) {
            return new ProductResponse(product.id(), product.name(), product.price(), product.stock(), product.likeCount(), product.brandName());
        }
    }

    public record GetProductsRequest(
        @Schema(name = "브랜드 ID")
        Long brandId,
        @Schema(name = "정렬 기준", example = "price_asc", allowableValues = {"price_asc", "price_desc", "latest", "likes_asc", "likes_desc"})
        String sort,
        @Schema(name = "페이지 번호")
        Integer page,
        @Schema(name = "페이지 크기")
        Integer size
    ) {
        public GetProductsRequest {
            if (page == null || page < 0) {
                page = 0;
            }
            if (size == null || size <= 0) {
                size = 20;
            }
        }
    }

}
