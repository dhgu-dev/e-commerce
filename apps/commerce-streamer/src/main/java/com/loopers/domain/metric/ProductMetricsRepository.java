package com.loopers.domain.metric;

import java.util.Optional;

public interface ProductMetricsRepository {
    ProductMetrics save(ProductMetrics productMetrics);

    Optional<ProductMetrics> findById(ProductMetricsId id);
}
