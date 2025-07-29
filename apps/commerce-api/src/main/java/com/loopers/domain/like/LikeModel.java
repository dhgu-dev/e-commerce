package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "likes")
@IdClass(LikeId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeModel {

    @Id
    private Long memberId;

    @Id
    private Long productId;

    private ZonedDateTime createdAt;

    public LikeModel(Long memberId, Long productId) {
        if (memberId == null || productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Member ID and Product ID cannot be null.");
        }

        this.memberId = memberId;
        this.productId = productId;
        this.createdAt = ZonedDateTime.now();
    }
}
