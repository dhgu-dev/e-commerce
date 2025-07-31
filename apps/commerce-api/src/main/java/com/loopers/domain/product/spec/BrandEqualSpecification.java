package com.loopers.domain.product.spec;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.product.QProductModel;
import com.loopers.support.Specification;
import com.querydsl.core.types.dsl.BooleanExpression;

public class BrandEqualSpecification implements Specification<QProductModel> {

    private final BrandModel brand;

    private BrandEqualSpecification(BrandModel brand) {
        this.brand = brand;
    }

    public static BrandEqualSpecification of(BrandModel brand) {
        return new BrandEqualSpecification(brand);
    }

    @Override
    public BooleanExpression isSatisfiedBy(QProductModel productModel) {
        if (brand == null) return null;
        return productModel.brandId.eq(brand.getId());
    }

}
