package com.loopers.infrastructure.metric;

import com.loopers.domain.metric.ProductMetrics;
import com.loopers.domain.metric.ProductMetricsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetrics, ProductMetricsId> {
}
