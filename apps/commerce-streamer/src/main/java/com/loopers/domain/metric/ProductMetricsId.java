package com.loopers.domain.metric;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ProductMetricsId implements Serializable {
    private Long productId;
    private LocalDate baseDate;

    public ProductMetricsId(Long productId, LocalDate baseDate) {
        this.productId = productId;
        this.baseDate = baseDate;
    }
}
