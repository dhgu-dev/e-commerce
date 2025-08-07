package com.loopers.interfaces.api.like;

import com.loopers.application.member.MemberInfo;
import com.loopers.application.product.dto.ProductInfo;
import com.loopers.domain.member.enums.Gender;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LikeV1Dto {
    public record LikeResponse(
        String userId,
        Long productId,
        boolean liked
    ) {
        public static LikeResponse from(String userId, Long productId, boolean liked) {
            return new LikeResponse(userId, productId, liked);
        }
    }

    public record GetLikedProductsRequest(
        Integer page,
        Integer size
    ) {
        public GetLikedProductsRequest {
            if (page == null || page < 0) {
                page = 0;
            }
            if (size == null || size <= 0) {
                size = 20;
            }
        }
    }

    public record GetLikedProductsResponse(
        MemberResponse member,
        Page<ProductResponse> products
    ) {
        public static GetLikedProductsResponse from(MemberResponse member, Page<ProductResponse> products) {
            return new GetLikedProductsResponse(member, products);
        }

        public record MemberResponse(String userId, Gender gender, LocalDate birthdate, String email, Long points) {
            public static MemberResponse from(MemberInfo member) {
                return new MemberResponse(
                    member.userId(),
                    member.gender(),
                    member.birthdate(),
                    member.email(),
                    member.points()
                );
            }
        }

        public record ProductResponse(
            Long id, String name, BigDecimal price, long likeCount, String brandName
        ) {
            public static ProductResponse from(ProductInfo product) {
                return new ProductResponse(product.id(), product.name(), product.price(), product.likeCount(), product.brandName());
            }
        }
    }
}
