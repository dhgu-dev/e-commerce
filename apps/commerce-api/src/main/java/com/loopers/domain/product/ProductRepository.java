package com.loopers.domain.product;

import com.loopers.domain.product.spec.ProductSearchCondition;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository {
    List<ProductModel> search(ProductSearchCondition condition, Pageable pageable);

    long getTotalAmount(ProductSearchCondition condition);

    List<ProductModel> findAll(Set<Long> productIds);

    Optional<ProductModel> find(Long productId);

    ProductModel save(ProductModel product);

    Optional<ProductModel> findWithLock(Long productId);
}
