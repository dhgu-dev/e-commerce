package com.loopers.application.brand.dto;

import com.loopers.application.product.dto.ProductInfo;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.product.ProductModel;

import java.util.List;

public record BrandInfo(Long id, String name, String description, List<ProductInfo> products) {

    public static BrandInfo from(BrandModel brand, List<ProductModel> products) {
        return new BrandInfo(
            brand.getId(),
            brand.getName(),
            brand.getDescription(),
            products.stream().map(product -> ProductInfo.from(product, brand)).toList()
        );
    }
}
