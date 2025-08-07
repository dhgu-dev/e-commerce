package com.loopers.interfaces.api.like;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Like V1 API")
public interface LikeV1ApiSpec {

    @Operation(
        summary = "상품 좋아요 등록"
    )
    ApiResponse<LikeV1Dto.LikeResponse> likeProduct(
        String userId,
        Long productId
    );

    @Operation(
        summary = "상품 좋아요 취소"
    )
    ApiResponse<LikeV1Dto.LikeResponse> unlikeProduct(
        String userId,
        Long productId
    );

    @Operation(
        summary = "내가 좋아요 한 상품 목록 조회"
    )
    ApiResponse<LikeV1Dto.GetLikedProductsResponse> getLikedProducts(
        String userId,
        LikeV1Dto.GetLikedProductsRequest dto
    );
}
