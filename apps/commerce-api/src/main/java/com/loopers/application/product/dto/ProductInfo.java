package com.loopers.application.product.dto;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.product.ProductModel;

import java.math.BigDecimal;

public record ProductInfo(Long id, String name, BigDecimal price, Long stock, int likeCount, String brandName) {

    public static ProductInfo from(ProductModel productModel, BrandModel brandModel) {
        return new ProductInfo(
            productModel.getId(),
            productModel.getName(),
            productModel.getPrice().getAmount(),
            productModel.getStock().getQuantity(),
            productModel.getLikeCount(),
            brandModel.getName()
        );
    }
}
