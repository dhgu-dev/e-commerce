package com.loopers.infrastructure.brand;

import com.loopers.domain.brand.BrandEvent;
import com.loopers.domain.brand.BrandEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrandCoreEventPublisher implements BrandEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(BrandEvent.BrandProductLikedEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(BrandEvent.BrandProductUnLikedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
