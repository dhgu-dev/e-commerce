package com.loopers.domain.like;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeId implements Serializable {
    private Long memberId;
    private Long productId;

    private LikeId(Long memberId, Long productId) {
        this.memberId = memberId;
        this.productId = productId;
    }

    public static LikeId of(Long memberId, Long productId) {
        return new LikeId(memberId, productId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeId likeId = (LikeId) o;
        return Objects.equals(memberId, likeId.memberId) && Objects.equals(productId, likeId.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, productId);
    }
}
