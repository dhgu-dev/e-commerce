package com.loopers.domain.brand;

import java.time.LocalDateTime;

public class BrandEvent {
    public record BrandProductLikedEvent(Long productId, Long brandId, Long memberId, LocalDateTime issuedAt) {
    }

    public record BrandProductUnLikedEvent(Long productId, Long brandId, Long memberId, LocalDateTime issuedAt) {
    }
}
