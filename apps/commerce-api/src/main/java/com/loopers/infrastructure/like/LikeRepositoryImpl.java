package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.member.MemberModel;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.loopers.domain.like.QLikeModel.likeModel;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<LikeModel> find(Long memberId, Long productId) {
        return Optional.ofNullable(
            queryFactory.selectFrom(likeModel)
                .where(likeModel.memberId.eq(memberId).and(likeModel.productId.eq(productId)))
                .fetchOne()
        );
    }

    @Override
    public LikeModel save(LikeModel likeModel) {
        return likeJpaRepository.save(likeModel);
    }

    @Override
    public void delete(LikeModel likeModel) {
        likeJpaRepository.delete(likeModel);
    }

    @Override
    public long getProductLikeCount(Long productId) {
        Long likeCount = queryFactory.select(likeModel.count())
            .from(likeModel)
            .where(likeModel.productId.eq(productId))
            .fetchOne();
        return likeCount != null ? likeCount : 0L;
    }

    @Override
    public List<LikeModel> search(MemberModel member, Pageable pageable) {
        return queryFactory.selectFrom(likeModel)
            .where(likeModel.memberId.eq(member.getId()))
            .orderBy(likeModel.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public long countMemberLikedProducts(Long memberId) {
        Long count = queryFactory.select(likeModel.count())
            .from(likeModel)
            .where(likeModel.memberId.eq(memberId))
            .fetchOne();
        return count != null ? count : 0L;
    }
}
