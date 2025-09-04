package com.loopers.domain.product;

import java.math.BigDecimal;

public record ProductInfo(Long id, String name, BigDecimal price, Long stock, long likeCount, String brandName) {
}
