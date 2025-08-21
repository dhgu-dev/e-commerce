package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.vo.Price;
import com.loopers.domain.product.vo.Stock;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "product", indexes = {
    @Index(name = "idx__product__brand_id__like_count", columnList = "brand_id, like_count"),
    @Index(name = "idx__product__brand_id__price", columnList = "brand_id, price"),
    @Index(name = "idx__product__brand_id__created_at", columnList = "brand_id, created_at"),
    @Index(name = "idx__product__like_count", columnList = "like_count"),
    @Index(name = "idx__product__price", columnList = "price"),
    @Index(name = "idx__product__created_at", columnList = "created_at"),
})
public class ProductModel extends BaseEntity {

    @Getter
    private String name;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price"))
    @Getter
    private Price price;

    @Embedded
    @AttributeOverride(name = "quantity", column = @Column(name = "stock"))
    @Getter
    private Stock stock;

    @Getter
    private Long brandId;

    @Getter
    private long likeCount;

    protected ProductModel() {
    }

    public ProductModel(String name, Price price, Stock stock, Long brandId) {
        if (name == null || name.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Product name cannot be null or blank.");
        }
        if (price == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Price cannot be null.");
        }
        if (stock == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Stock cannot be null.");
        }
        if (brandId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Brand ID cannot be null.");
        }

        this.name = name;
        this.price = price;
        this.stock = stock;
        this.brandId = brandId;
        this.likeCount = 0L;
    }

    public void decreaseStock(long quantity) {
        this.stock = this.stock.deduct(quantity);
    }

    public void updateLikeCount(long likeCount) {
        if (likeCount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Like count cannot be negative.");
        }
        this.likeCount = likeCount;
    }

    public void restoreStock(long quantity) {
        this.stock = this.stock.restore(quantity);
    }
}
