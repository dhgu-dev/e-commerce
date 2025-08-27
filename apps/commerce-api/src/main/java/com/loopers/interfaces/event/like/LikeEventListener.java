package com.loopers.interfaces.event.like;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LikeEventListener {

    private final ProductService productService;
    private final LikeService likeService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeEvent.LikeMarkedEvent event) {
        var product = productService.getDetail(event.productId());
        productService.updateProductLikeCount(product, likeService.getLikeCount(product));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeEvent.LikeUnmarkedEvent event) {
        var product = productService.getDetail(event.productId());
        productService.updateProductLikeCount(product, likeService.getLikeCount(product));
    }
}
