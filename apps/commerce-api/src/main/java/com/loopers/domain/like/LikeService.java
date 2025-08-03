package com.loopers.domain.like;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.product.ProductModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void like(MemberModel member, ProductModel product) {
        if (member == null || product == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Member and Product cannot be null");
        }

        Optional<LikeModel> like = likeRepository.find(member.getId(), product.getId());

        if (like.isEmpty()) {
            likeRepository.save(new LikeModel(member.getId(), product.getId()));
        }
    }

    public void unlike(MemberModel member, ProductModel product) {
        if (member == null || product == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Member and Product cannot be null");
        }

        Optional<LikeModel> like = likeRepository.find(member.getId(), product.getId());
        like.ifPresent(likeRepository::delete);
    }

    public long getLikeCount(ProductModel product) {
        if (product == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product cannot be null");
        }

        return likeRepository.getProductLikeCount(product.getId());
    }

    public List<LikeModel> getLikes(MemberModel member, Pageable pageable) {
        if (member == null || pageable == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Member and Pageable cannot be null");
        }
        return likeRepository.search(member, pageable);
    }

    public long countLikedProducts(MemberModel member) {
        if (member == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Member cannot be null");
        }
        return likeRepository.countMemberLikedProducts(member.getId());
    }
}
