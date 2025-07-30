package com.loopers.domain.product;

import com.loopers.domain.product.spec.ProductSearchCondition;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductModel> search(ProductSearchCondition condition, Pageable pageable) {
        if (condition == null || pageable == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Search condition or pageable cannot be null");
        }

        return productRepository.search(condition, pageable);
    }

    public long countAll(ProductSearchCondition condition) {
        if (condition == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Search condition cannot be null");
        }

        return productRepository.getTotalAmount(condition);
    }

    public List<ProductModel> getProducts(Set<Long> productIds) {
        if (productIds == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product IDs cannot be null");
        }

        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }

        return productRepository.findAll(productIds);
    }

    public ProductModel getDetail(Long productId) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product ID cannot be null");
        }

        Optional<ProductModel> product = productRepository.find(productId);
        if (product.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "Product not found with ID: " + productId);
        }

        return product.get();
    }
}
