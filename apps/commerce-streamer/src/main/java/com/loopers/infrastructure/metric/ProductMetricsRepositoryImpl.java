package com.loopers.infrastructure.metric;

import com.loopers.domain.metric.ProductMetrics;
import com.loopers.domain.metric.ProductMetricsId;
import com.loopers.domain.metric.ProductMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductMetricsRepositoryImpl implements ProductMetricsRepository {

    private final ProductMetricsJpaRepository productMetricsJpaRepository;

    @Override
    public ProductMetrics save(ProductMetrics productMetrics) {
        return productMetricsJpaRepository.save(productMetrics);
    }

    @Override
    public Optional<ProductMetrics> findById(ProductMetricsId id) {
        return productMetricsJpaRepository.findById(id);
    }
}
