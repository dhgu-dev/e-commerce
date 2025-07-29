package com.loopers.domain.orders.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSnapshot implements Serializable {

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String name;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "product_price"))
    private Price price;

    private ProductSnapshot(Long productId, String name, Price price) {
        if (productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product ID cannot be null");
        }
        if (name == null || name.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product name cannot be null or empty");
        }
        if (price == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product price cannot be null");
        }
        
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

    public static ProductSnapshot of(Long productId, String name, Price price) {
        return new ProductSnapshot(productId, name, price);
    }
}
