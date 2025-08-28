package com.loopers.domain.brand;

public interface BrandEventPublisher {
    void publish(BrandEvent.BrandProductLikedEvent event);

    void publish(BrandEvent.BrandProductUnLikedEvent event);
}
