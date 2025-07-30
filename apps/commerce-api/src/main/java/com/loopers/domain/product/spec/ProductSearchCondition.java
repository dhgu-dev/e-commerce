package com.loopers.domain.product.spec;

import com.loopers.domain.brand.BrandModel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PACKAGE)
public class ProductSearchCondition {

    @Getter
    private BrandModel brand;


}
