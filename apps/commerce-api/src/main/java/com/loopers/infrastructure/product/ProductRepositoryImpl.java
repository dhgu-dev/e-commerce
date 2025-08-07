package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.enums.OrderBy;
import com.loopers.domain.product.spec.BrandEqualSpecification;
import com.loopers.domain.product.spec.ProductSearchCondition;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.loopers.domain.product.QProductModel.productModel;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductModel> search(ProductSearchCondition condition, Pageable pageable) {
        if (condition == null || pageable == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Search condition or pageable cannot be null");
        }

        BooleanBuilder predicates = new BooleanBuilder();
        predicates.and(BrandEqualSpecification.of(condition.getBrand()).isSatisfiedBy(productModel));

        List<Pair<String, Direction>> sorts = pageable.getSort().stream().map(order -> Pair.of(order.getProperty(), order.getDirection())).toList();
        return queryFactory.selectFrom(productModel)
            .where(predicates)
            .orderBy(sorts.stream().map(this::toOrderBy).toArray(OrderSpecifier<?>[]::new))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public long getTotalAmount(ProductSearchCondition condition) {
        if (condition == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Search condition cannot be null");
        }

        BooleanBuilder predicates = new BooleanBuilder();
        predicates.and(BrandEqualSpecification.of(condition.getBrand()).isSatisfiedBy(productModel));

        Long totalCount = queryFactory.select(productModel.count())
            .from(productModel)
            .where(predicates)
            .fetchOne();
        return totalCount != null ? totalCount : 0L;
    }

    @Override
    public List<ProductModel> findAll(Set<Long> productIds) {
        if (productIds == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product IDs cannot be null");
        }

        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }

        return queryFactory.selectFrom(productModel)
            .where(productModel.id.in(productIds))
            .fetch();
    }

    @Override
    public Optional<ProductModel> find(Long productId) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product ID cannot be null");
        }

        return Optional.ofNullable(queryFactory.selectFrom(productModel)
            .where(productModel.id.eq(productId))
            .fetchOne());
    }

    @Override
    public ProductModel save(ProductModel product) {
        return productJpaRepository.save(product);
    }

    private OrderSpecifier<?> toOrderBy(Pair<String, Direction> sort) {
        final String property = sort.getFirst();
        final Direction direction = sort.getSecond();
        return switch (OrderBy.fromValue(property)) {
            case LATEST -> direction.isAscending() ? productModel.createdAt.asc() : productModel.createdAt.desc();
            case LIKES -> direction.isAscending() ? productModel.likeCount.asc() : productModel.likeCount.desc();
            case PRICE -> direction.isAscending() ? productModel.price.amount.asc() : productModel.price.amount.desc();
        };
    }

    @Override
    public Optional<ProductModel> findWithLock(Long productId) {
        return Optional.ofNullable(queryFactory.selectFrom(productModel)
            .where(productModel.id.eq(productId))
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()
        );
    }
}
