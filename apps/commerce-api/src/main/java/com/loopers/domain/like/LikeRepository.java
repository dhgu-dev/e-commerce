package com.loopers.domain.like;

import com.loopers.domain.member.MemberModel;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {
    Optional<LikeModel> find(Long memberId, Long productId);

    LikeModel save(LikeModel likeModel);

    void delete(LikeModel likeModel);

    long getProductLikeCount(Long productId);

    List<LikeModel> search(MemberModel member, Pageable pageable);

    long countMemberLikedProducts(Long memberId);
}
