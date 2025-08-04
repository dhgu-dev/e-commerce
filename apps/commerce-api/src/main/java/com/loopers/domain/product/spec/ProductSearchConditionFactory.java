package com.loopers.domain.product.spec;

import com.loopers.domain.brand.BrandModel;

public class ProductSearchConditionFactory {

    public static ProductSearchCondition buildNoCondition() {
        return ProductSearchCondition.builder()
                .build();
    }

    public static ProductSearchCondition buildBrandEqual(BrandModel brand) {
        return ProductSearchCondition.builder()
                .brand(brand)
                .build();
    }
}
