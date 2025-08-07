package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.usecase.query.QueryBrandDetailUseCase;
import com.loopers.application.brand.usecase.query.QueryBrandDetailUseCase.Query;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
public class BrandV1Controller implements BrandV1ApiSpec {

    private final QueryBrandDetailUseCase queryBrandDetailUseCase;

    @Override
    @GetMapping("/{brandId}")
    public ApiResponse<BrandV1Dto.BrandResponse> getBrand(@PathVariable("brandId") Long brandId) {
        return ApiResponse.success(BrandV1Dto.BrandResponse.from(
            queryBrandDetailUseCase.execute(new Query(brandId)).brandInfo()
        ));
    }

}
