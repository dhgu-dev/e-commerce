package com.loopers.infrastructure.brand;

import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.loopers.domain.brand.QBrandModel.brandModel;


@Repository
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<BrandModel> find(Long brandId) {
        if (brandId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product ID cannot be null");
        }

        return Optional.ofNullable(
            queryFactory.selectFrom(brandModel)
                .where(brandModel.id.eq(brandId))
                .fetchOne()
        );
    }

    @Override
    public List<BrandModel> findAll(Set<Long> brandIds) {
        if (brandIds == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Brand IDs cannot be null");
        }

        if (brandIds.isEmpty()) {
            return Collections.emptyList();
        }

        return queryFactory.selectFrom(brandModel)
            .where(brandModel.id.in(brandIds))
            .fetch();
    }
}
